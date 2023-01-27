package jp.co.yumemi.android.code_check.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import dagger.hilt.android.qualifiers.ActivityContext
import jp.co.yumemi.android.code_check.utils.YUVUtil
import java.nio.ByteBuffer
import java.util.*
import javax.inject.Inject


class CameraCapture @Inject constructor(@ActivityContext val context: Context) {

    private val TAG = CameraCapture::class.simpleName

    private val cameraManager: CameraManager =
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

    private lateinit var cameraId: String
    private lateinit var backgroundHandlerThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var previewDataReader: ImageReader
    private lateinit var captureDataReader: ImageReader
    private lateinit var previewSurface: Surface
    private var jpegOrientation: Int = 0
    private var cameraPreviewDataListener: ICameraPreviewDataListener? = null
    private var cameraTakePhotoListener: ICameraTakePhotoListener? = null

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
    }

    /**
     * Capture State Callback
     */
    private val captureSessionStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e(TAG, "capture session onConfigureFailed!!")
        }

        override fun onConfigured(session: CameraCaptureSession) {
            cameraCaptureSession = session

            val previewRequestBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            previewRequestBuilder.addTarget(previewSurface)
            previewRequestBuilder.addTarget(previewDataReader.surface)
            cameraCaptureSession.setRepeatingRequest(
                previewRequestBuilder.build(), null, backgroundHandler
            )
        }
    }

    fun setupCamera(
        previewSurface: Surface,
        cameraDataListener: ICameraPreviewDataListener? = null,
        cameraTakePhotoListener: ICameraTakePhotoListener? = null,
    ) {
        this.previewSurface = previewSurface
        this.cameraPreviewDataListener = cameraDataListener
        this.cameraTakePhotoListener = cameraTakePhotoListener
        val cameraIds: Array<String> = cameraManager.cameraIdList

        for (id in cameraIds) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)

            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                continue
            }

            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.let { streamConfigurationMap ->
                    val photoSize = streamConfigurationMap.getOutputSizes(
                        ImageFormat.YUV_420_888
                    ).maxByOrNull { it.height * it.width }!!

                    Log.e(
                        TAG, "photoSize Width is ${photoSize.width}, Height is ${photoSize.height}"
                    )

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
                }
            cameraId = id
        }
    }

    /**
     * ImageAvailable Listener
     */
    private val onPreviewImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image: Image = reader.acquireNextImage()
        YUVUtil.yuv420888toNV12(image)?.let {
            this.cameraPreviewDataListener?.previewNV12DataListener(it)
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
            cameraDevice.createCaptureSession(
                listOf(previewSurface, previewDataReader.surface, captureDataReader.surface),
                captureSessionStateCallback,
                null
            )
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
        captureRequestBuilder.addTarget(captureDataReader.surface)
        jpegOrientation = orientations.get(rotation)
        cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
    }

    @SuppressLint("MissingPermission")
    fun connectCamera() {
        cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler)
    }

}