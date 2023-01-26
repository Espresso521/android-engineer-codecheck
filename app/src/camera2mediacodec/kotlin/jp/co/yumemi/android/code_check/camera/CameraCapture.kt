package jp.co.yumemi.android.code_check.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.Image
import android.media.Image.Plane
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.ImageView
import dagger.hilt.android.qualifiers.ActivityContext
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

    private var orientations: SparseIntArray = SparseIntArray(4).apply {
        append(Surface.ROTATION_0, 0)
        append(Surface.ROTATION_90, 90)
        append(Surface.ROTATION_180, 180)
        append(Surface.ROTATION_270, 270)
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

    fun setupCamera(previewSurface: Surface) {
        this.previewSurface = previewSurface
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
                        TAG,
                        "photoSize Width is ${photoSize.width}, Height is ${photoSize.height}"
                    )
                    previewDataReader = ImageReader.newInstance(
                        photoSize.width, photoSize.height, ImageFormat.YUV_420_888, 1
                    )
                    previewDataReader.setOnImageAvailableListener(
                        onPreviewImageAvailableListener, backgroundHandler
                    )

                    val captureSize = streamConfigurationMap.getOutputSizes(
                        ImageFormat.JPEG
                    ).maxByOrNull { it.height * it.width }!!

                    captureDataReader = ImageReader.newInstance(
                        captureSize.width, captureSize.height, ImageFormat.JPEG, 1
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
        //Toast.makeText(context, "Photo Taken!", Toast.LENGTH_SHORT).show()
        //Log.d(TAG, "image available")
        val image: Image = reader.acquireNextImage()
        image.close()
    }

    private val onCaptureImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        Log.d(TAG, "capture image available")
        val image: Image = reader.acquireNextImage()
        val planes: Array<Plane> = image.planes
        val buffer = planes[0].buffer
        buffer.rewind()
        val data = ByteArray(buffer.capacity())
        buffer[data]
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        image.close()
        imageView.post { imageView.setImageBitmap(bitmap) }
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

    private lateinit var imageView: ImageView

    fun takePhoto(rotation: Int, imageView: ImageView) {
        this.imageView = imageView
        val captureRequestBuilder =
            cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureRequestBuilder.addTarget(captureDataReader.surface)
        //captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientations.get(rotation))
        cameraCaptureSession.capture(captureRequestBuilder.build(), null, null)
    }

    @SuppressLint("MissingPermission")
    fun connectCamera() {
        cameraManager.openCamera(cameraId, cameraStateCallback, backgroundHandler)
    }

}