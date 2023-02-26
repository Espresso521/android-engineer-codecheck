package jp.co.yumemi.android.code_check.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import dagger.hilt.android.qualifiers.ActivityContext
import jp.co.yumemi.android.code_check.data.EncodeDecodeDataRepo
import jp.co.yumemi.android.code_check.utils.FileUtils
import jp.co.yumemi.android.code_check.utils.YUVUtil
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class CameraCapture @Inject constructor(
    @ActivityContext val context: Context, var encodeDecodeDataRepo: EncodeDecodeDataRepo
) {

    private val TAG = CameraCapture::class.simpleName

    private val cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private lateinit var cameraId: String
    private lateinit var backgroundHandlerThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private lateinit var cameraCharacteristics: CameraCharacteristics
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var previewDataReader: ImageReader // for video encode
    private lateinit var captureDataReader: ImageReader // for take photo
    private lateinit var previewSurface: Surface // for UI preview
    private lateinit var previewRequestBuilder: CaptureRequest.Builder
    private lateinit var recordRequestBuilder: CaptureRequest.Builder
    private lateinit var currentRequestBuilder: CaptureRequest.Builder
    private lateinit var mediaRecorder: MediaRecorder

    private var jpegOrientation: Int = 0
    private var cameraTakePhotoListener: ICameraTakePhotoListener? = null

    /* 缩放相关 */
    private val maxZOOM = 100 // 放大的最大值，用于计算每次放大/缩小操作改变的大小
    private var mStepWidth = 0.0f // 每次改变的宽度大小
    private var mStepHeight = 0.0f // 每次改变的高度大小

    private var orientations: SparseIntArray = SparseIntArray(4).apply {
        append(Surface.ROTATION_0, 90)
        append(Surface.ROTATION_90, 0)
        append(Surface.ROTATION_180, 270)
        append(Surface.ROTATION_270, 180)
    }

    fun startBackgroundThread() {
        backgroundHandlerThread = HandlerThread("CameraVideoThread")
        backgroundHandlerThread.start()
        backgroundHandler = Handler(backgroundHandlerThread.looper)
    }

    fun stopBackgroundThread() {
        backgroundHandlerThread.quitSafely()
        backgroundHandlerThread.join()
    }

    //关闭相机
    fun closeCamera() {
        cameraDevice.close()
        cameraCaptureSession.close()
        previewDataReader.close()
        captureDataReader.close()
    }

    private fun initZoomParameter() {
        val rect =
            cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        // max_digital_zoom 表示 active_rect 除以 crop_rect 的最大值
        val maxDigitalZoom =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)

        // crop_rect的最小宽高
        val minWidth = rect!!.width() / maxDigitalZoom!!
        val minHeight = rect.height() / maxDigitalZoom
        // 因为缩放时两边都要变化，所以要除以2
        mStepWidth = (rect.width() - minWidth) / maxZOOM / 2
        mStepHeight = (rect.height() - minHeight) / maxZOOM / 2
    }

    private fun initPreviewRequestAndStart() {
        previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        // 设置预览输出的 Surface
        previewRequestBuilder.addTarget(previewSurface) // for surface view
        previewRequestBuilder.addTarget(previewDataReader.surface) // for encoder
        // 设置连续自动对焦
        previewRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )
        // 设置自动白平衡
        previewRequestBuilder.set(
            CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO
        )

        currentRequestBuilder = previewRequestBuilder

        cameraCaptureSession.setRepeatingRequest(
            previewRequestBuilder.build(), null, backgroundHandler
        )
    }

    fun startPreview() {
        cameraDevice.createCaptureSession(
            listOf(previewSurface, previewDataReader.surface, captureDataReader.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "capture session onConfigureFailed!!")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    initZoomParameter()
                    initPreviewRequestAndStart()
                }
            },
            null
        )
    }

    fun handleZoom(zoom: Int) {
        var mZoom = zoom
        if (mZoom > maxZOOM) mZoom = maxZOOM
        if (mZoom <= 0) mZoom = 1

        val rect: Rect? =
            cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val cropW = (mStepWidth * mZoom).toInt()
        val cropH = (mStepHeight * mZoom).toInt()
        val zoomRect =
            Rect(rect!!.left + cropW, rect.top + cropH, rect.right - cropW, rect.bottom - cropH)

        currentRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect)
        cameraCaptureSession.stopRepeating()
        cameraCaptureSession.capture(currentRequestBuilder.build(), object : CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                cameraCaptureSession.setRepeatingRequest(
                    currentRequestBuilder.build(), null, backgroundHandler
                )
            }
        }, null)
    }

    fun setupCamera(
        previewSurface: Surface,
        cameraTakePhotoListener: ICameraTakePhotoListener? = null,
    ) {
        this.previewSurface = previewSurface
        this.cameraTakePhotoListener = cameraTakePhotoListener
        val cameraIds: Array<String> = cameraManager.cameraIdList

        for (id in cameraIds) {
            if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING)
                == CameraCharacteristics.LENS_FACING_FRONT
            ) {
                continue
            }

            // all reader use 1080P parameter
            previewDataReader = ImageReader.newInstance(
                1920, 1080, ImageFormat.YUV_420_888, 1
            )
            previewDataReader.setOnImageAvailableListener(
                onPreviewImageAvailableListener, backgroundHandler
            )

            captureDataReader = ImageReader.newInstance(
                1920, 1080, ImageFormat.JPEG, 1
            )
            captureDataReader.setOnImageAvailableListener(
                onCaptureImageAvailableListener, backgroundHandler
            )

            cameraId = id
        }

        cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
    }

    /**
     * ImageAvailable Listener
     */
    private val onPreviewImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image: Image = reader.acquireNextImage()
        YUVUtil.yuv420888toNV12(image)?.let {
            encodeDecodeDataRepo.previewNV12DataListener(it)
        }
        image.close()
    }

    private val onCaptureImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        Log.d(TAG, "capture image available")
        val image: Image = reader.acquireNextImage()
        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        buffer.rewind()
        val data = ByteArray(buffer.capacity())
        buffer[data]
        this.cameraTakePhotoListener?.takePhotoDataListener(jpegOrientation, data)
        image.close()
    }

    /**
     * Camera State Callbacks
     */
    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview()
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            //
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            val errorMsg = when (error) {
                ERROR_CAMERA_DEVICE -> "Fatal (device)"
                ERROR_CAMERA_DISABLED -> "Device policy"
                ERROR_CAMERA_IN_USE -> "Camera in use"
                ERROR_CAMERA_SERVICE -> "Fatal (service)"
                ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                else -> "Unknown"
            }
            Log.e(TAG, "Error when trying to connect camera $errorMsg")
        }
    }

    fun takePhoto(rotation: Int) {
        val captureRequestBuilder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE,
            CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )
        currentRequestBuilder.get(CaptureRequest.SCALER_CROP_REGION)?.let { zoomRect ->
            captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoomRect)
        }
        captureRequestBuilder.addTarget(captureDataReader.surface)
        jpegOrientation = orientations.get(rotation)
        cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
    }

    @SuppressLint("MissingPermission")
    fun connectCamera() {
        cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler)
    }

    private fun setupMediaRecorder() {
        val fileName = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.JAPAN
        ).format(Date(System.currentTimeMillis()))
        val file =
            File(FileUtils.getMediaFileDir(context), "record_$fileName.mp4")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(file.path)
            setVideoEncodingBitRate(1024 * 1024 * 1024)
            setVideoFrameRate(30)
            setVideoSize(1920, 1080)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setOrientationHint(90)
            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace();
            }
        }
    }

    fun startRecord() {
        setupMediaRecorder()

        recordRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD)
        // 设置预览输出的 Surface
        recordRequestBuilder.addTarget(mediaRecorder.surface) // for encoder
        recordRequestBuilder.addTarget(previewSurface) // for surface view
        previewRequestBuilder.addTarget(previewDataReader.surface) // for encoder
        // 设置连续自动对焦
        recordRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        )
        // 设置自动白平衡
        recordRequestBuilder.set(
            CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO
        )

        currentRequestBuilder = recordRequestBuilder

        cameraCaptureSession.stopRepeating()

        cameraDevice.createCaptureSession(
            listOf(previewSurface, mediaRecorder.surface, previewDataReader.surface, captureDataReader.surface),
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "capture session onConfigureFailed!!")
                }

                override fun onConfigured(session: CameraCaptureSession) {
                    cameraCaptureSession = session
                    cameraCaptureSession.capture(recordRequestBuilder.build(), object : CaptureCallback() {
                        override fun onCaptureCompleted(
                            session: CameraCaptureSession,
                            request: CaptureRequest,
                            result: TotalCaptureResult
                        ) {
                            cameraCaptureSession.setRepeatingRequest(
                                recordRequestBuilder.build(), null, backgroundHandler
                            )
                            mediaRecorder.start()
                        }
                    }, null)
                }
            },
            null
        )
    }

    fun stopRecord() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        cameraCaptureSession.stopRepeating()
        startPreview()
    }
}