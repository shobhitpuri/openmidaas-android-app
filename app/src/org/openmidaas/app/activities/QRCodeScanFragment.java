package org.openmidaas.app.activities;

import java.io.IOException;
import java.util.Collection;

import org.openmidaas.app.R;
import org.openmidaas.app.activities.barcodeSupport.BarCodeHandler;
import org.openmidaas.app.common.DialogUtils;
import org.openmidaas.app.common.Logger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.abhi.barcode.fragment.barcode.ViewfinderView;
import com.abhi.barcode.fragment.dialogs.IDialogCreator;
import com.abhi.barcode.fragment.interfaces.IConstants;
import com.abhi.barcode.fragment.interfaces.IResultCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;

public class QRCodeScanFragment extends Fragment implements
		SurfaceHolder.Callback, IConstants, IDialogCreator {

	private Result lastResult;
	
	private CameraManager cameraManager;
	private BarCodeHandler handler;
	private Result savedResultToShow;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private boolean runCamera = false;
	private IResultCallback mCallBack;

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	@SuppressWarnings("deprecation")
	public void startCameraCampure() {
		cameraManager = new CameraManager(getActivity().getApplicationContext());
		viewfinderView.setCameraManager(cameraManager);
		handler = null;
		resetStatusView();
		runCamera = true;
		SurfaceView surfaceView = (SurfaceView) getView().findViewById(
				R.id.cameraView);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		hasSurface = true;
		if (hasSurface) {
			initCamera(surfaceHolder, viewfinderView);
		} else {
			surfaceHolder.addCallback(QRCodeScanFragment.this);
			//Depreciated in API 11. Automaticaly set for versions above that
			if(android.os.Build.VERSION.SDK_INT<11){
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
		}
		decodeFormats = null;
		characterSet = null;
	}

	public void stopCameraCapture() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) getView().findViewById(
					R.id.cameraView);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(QRCodeScanFragment.this);
		}
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View c = View.inflate(getActivity(), R.layout.superimposedcamera, null);
		viewfinderView = (ViewfinderView) c.findViewById(R.id.viewFinder_View);
		hasSurface = false;
		runCamera = true;
		return c;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResume() {
		super.onResume();
		if (runCamera && hasSurface) {
			startCameraCampure();
		} else if (runCamera) {
			SurfaceView surfaceView = (SurfaceView) getView().findViewById(
					R.id.cameraView);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.addCallback(QRCodeScanFragment.this);
			//Depreciated in API 11. Automatically set for versions above that
			if(android.os.Build.VERSION.SDK_INT<11){
				surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			}
		}
	}

	@Override
	public void onPause() {
		if (runCamera) {
			stopCameraCapture();
		}
		super.onPause();
	}

	private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
		// Bitmap isn't used yet -- will be used soon
		if (handler == null) {
			savedResultToShow = result;
		} else {
			if (result != null) {
				savedResultToShow = result;
			}
			if (savedResultToShow != null) {
				Message message = Message.obtain(handler, DECODE_COMPLETE,
						savedResultToShow);
				handler.sendMessage(message);
			}
			savedResultToShow = null;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Logger.error(getClass(),
					"*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			if (runCamera)
				startCameraCampure();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult : The contents of the barcode.
	 * @param barcode : A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(Result rawResult, Bitmap barcode) {
		drawResultPoints(barcode, rawResult);
		Logger.error(getClass(), "Value recived: " + rawResult.getText());
		lastResult = rawResult;
		if (mCallBack != null) {
			mCallBack.result(lastResult);
			DialogUtils.showNeutralButtonDialog(getActivity(), "Error", "Error in scan");
		} else {
			((MainTabActivity)getActivity()).processUrl(rawResult.getText());
		}
	}

	/**
	 * Superimpose a line for 1D or dots for 2D to highlight the key features of
	 * the barcode.
	 * 
	 * @param barcode : A bitmap of the captured image.
	 * @param rawResult : The decoded results which contains the points to draw.
	 */
	private void drawResultPoints(Bitmap barcode, Result rawResult) {
		ResultPoint[] points = rawResult.getResultPoints();
		if (points != null && points.length > 0) {
			Canvas canvas = new Canvas(barcode);
			Paint paint = new Paint();
			paint.setColor(getResources().getColor(R.color.result_image_border));
			paint.setStrokeWidth(3.0f);
			paint.setStyle(Paint.Style.STROKE);
			Rect border = new Rect(2, 2, barcode.getWidth() - 2,
					barcode.getHeight() - 2);
			canvas.drawRect(border, paint);

			paint.setColor(getResources().getColor(R.color.result_points));
			if (points.length == 2) {
				paint.setStrokeWidth(4.0f);
				drawLine(canvas, paint, points[0], points[1]);
			} else if (points.length == 4
					&& (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A || rawResult
							.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
				drawLine(canvas, paint, points[0], points[1]);
				drawLine(canvas, paint, points[2], points[3]);
			} else {
				paint.setStrokeWidth(10.0f);
				for (ResultPoint point : points) {
					canvas.drawPoint(point.getX(), point.getY(), paint);
				}
			}
		}
	}

	private static void drawLine(Canvas canvas, Paint paint, ResultPoint a,
			ResultPoint b) {
		canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
	}

	private void initCamera(SurfaceHolder surfaceHolder, View v) {
		try {
			cameraManager.openDriver(surfaceHolder, v);
			if (handler == null) {
				handler = new BarCodeHandler(this, decodeFormats, characterSet,
						cameraManager);
			}
			decodeOrStoreSavedBitmap(null, null);
		} catch (IOException ioe) {
			Logger.warn(getClass(), ioe);
		} catch (RuntimeException e) {
			Logger.warn(getClass(), "Unexpected error initializing camera: " + e);
		}
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(RESTART_PREVIEW, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		viewfinderView.setVisibility(View.VISIBLE);
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public Dialog createDialog(int mWhat) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("QR Value");
		builder.setMessage(lastResult.getText());
		builder.setPositiveButton("Details",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// DO NOTHING
					}
				}).setNegativeButton("Scan Again",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						startCameraCampure();
					}
				});
		return builder.create();
	}

	public IResultCallback getmCallBack() {
		return mCallBack;
	}

	public void setmCallBack(IResultCallback mCallBack) {
		this.mCallBack = mCallBack;
	}
}