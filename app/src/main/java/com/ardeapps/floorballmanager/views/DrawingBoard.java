package com.ardeapps.floorballmanager.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ardeapps.floorballmanager.AppRes;
import com.ardeapps.floorballmanager.R;
import com.ardeapps.floorballmanager.dialogFragments.TacticSettingsDialogFragment;
import com.ardeapps.floorballmanager.fragments.TacticBoardFragment;
import com.ardeapps.floorballmanager.utils.Logger;

import java.util.ArrayList;

import static com.ardeapps.floorballmanager.fragments.TacticBoardFragment.Tool;

/**
 * Created by Arttu on 15.11.2019.
 */

public class DrawingBoard extends android.support.v7.widget.AppCompatImageView {

    private Bitmap mBitmap;
    private Canvas finalCanvas;
    private Path currentPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint paint;

    private Tool selectedTool;
    private int currentColor;
    private int currentSize;

    private float startX, startY;
    private static final float TOUCH_TOLERANCE = 4;

    private ArrayList<Object> drawings = new ArrayList<>();

    private class Cross {
        public Path crossPath;
        public Paint crossPaint;
    }

    private class Circle {
        public Path circlePath;
        public Paint circlePaint;
    }

    private class Pen {
        public Path path;
        public Paint pathPaint;
    }

    private class Arrow {
        public Path arrowPath;
        public Paint arrowPaint;
    }

    private class DottedArrow {
        public Path arrow;
        public Path wings;
        public Paint arrowPaint;
        public Paint wingsPaint;
    }

    public DrawingBoard(Context context) {
        super(context);
        createView(context);
    }

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        createView(context);
    }

    public void setBackgroundField(TacticSettingsDialogFragment.Field field) {
        if (field == TacticSettingsDialogFragment.Field.FULL) {
            setImageResource(R.drawable.floorball_field);
        } else if (field == TacticSettingsDialogFragment.Field.HALF_LEFT){
            setImageResource(R.drawable.floorball_field_rotated);
            // TODO ei toimi
            setRotation(180f);
        } else if (field == TacticSettingsDialogFragment.Field.HALF_RIGHT) {
            setImageResource(R.drawable.floorball_field_rotated);
        }
    }

    public void setPaintColor(int color) {
        currentColor = ContextCompat.getColor(AppRes.getContext(), color);
        paint.setColor(currentColor);
    }

    public void setPaintSize(int size) {
        currentSize = size;
        paint.setStrokeWidth(currentSize);
    }

    public void setSelectedTool(TacticBoardFragment.Tool tool) {
        selectedTool = tool;
    }

    public void createView(Context c) {
        context = c;
        currentPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        // Set defaults
        currentColor = ContextCompat.getColor(AppRes.getContext(), R.color.color_red_light);
        currentSize = 10;

        setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(currentColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(currentSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        finalCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Logger.log("ON DRAW");
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(currentPath, paint);
    }

    public void restore() {
        for(Object drawing : drawings) {
            if(drawing instanceof Arrow) {
                Arrow arrow = (Arrow) drawing;
                finalCanvas.drawPath(arrow.arrowPath, arrow.arrowPaint);
            }
        }
        invalidate();
    }

    public void clear() {
        finalCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    private void drawCircle(float positionX, float positionY) {
        finalCanvas.drawCircle(positionX, positionY, currentSize, paint);
    }

    private void drawCross(float positionX, float positionY) {
        float halfSize = (float)currentSize / 2;
        float leftX = positionX - halfSize;
        float rightX = positionX + halfSize;
        float topY = positionY - halfSize;
        float bottomY = positionY + halfSize;

        finalCanvas.drawLine(leftX, topY, rightX, bottomY, paint);
        finalCanvas.drawLine(leftX, bottomY, rightX, topY, paint);
    }

    private void arrow_start(float x, float y) {
        startX = x;
        startY = y;
    }

    private void arrow_move(float x, float y, boolean dotted) {
        float dx = Math.abs(x - startX);
        float dy = Math.abs(y - startY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            if(dotted) {
                paint.setPathEffect(new DashPathEffect(new float[]{10, 40,}, 0));
            } else {
                paint.setPathEffect(null);
            }

            currentPath.reset();
            currentPath.moveTo(startX, startY);
            currentPath.lineTo(x, y);

            double angle = Math.toDegrees(Math.atan2(y - startY, x - startX)) + 90;
            int lineLength = 20;

            Matrix matrix = new Matrix();
            Path wings = new Path();
            wings.moveTo(x, y);
            wings.lineTo(x - lineLength, y + lineLength);
            wings.moveTo(x, y);
            wings.lineTo(x + lineLength, y + lineLength);
            matrix.setRotate((float)angle, x, y);
            wings.transform(matrix);
            currentPath.addPath(wings);
        }
    }

    private void arrow_up() {
        Arrow arrow = new Arrow();
        arrow.arrowPath = new Path(currentPath);
        arrow.arrowPaint = new Paint(paint);
        drawings.add(arrow);

        finalCanvas.drawPath(currentPath, paint);
        currentPath.reset();
    }

    private void pen_start(float x, float y) {
        currentPath.reset();
        currentPath.moveTo(x, y);
        startX = x;
        startY = y;
    }

    private void pen_move(float x, float y) {
        float dx = Math.abs(x - startX);
        float dy = Math.abs(y - startY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            currentPath.quadTo(startX, startY, (x + startX) / 2, (y + startY) / 2);
            startX = x;
            startY = y;
        }
    }

    private void pen_up() {
        currentPath.lineTo(startX, startY);
        // commit the path to our offscreen
        finalCanvas.drawPath(currentPath, paint);
        // kill this so we don't double draw
        currentPath.reset();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (selectedTool) {
            case PEN:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pen_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        pen_move(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        pen_up();
                        invalidate();
                        break;
                }
                break;
            case ARROW:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        arrow_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        arrow_move(x, y, false);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        arrow_up();
                        invalidate();
                        break;
                }
                break;
            case DOTTED_ARROW:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        arrow_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        arrow_move(x, y, true);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        arrow_up();
                        invalidate();
                        break;
                }
                break;
            case CROSS:
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    drawCross(x, y);
                    invalidate();
                }
                break;
            case CIRCLE:
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    drawCircle(x, y);
                    invalidate();
                }
                break;
        }

        return true;
    }

 /*private void drawPoint(float positionX, float positionY) {
        IconView iconView = new IconView(AppRes.getActivity());
        iconView.setText(AppRes.getContext().getString(R.string.icon_circle));
        iconView.setFont(AppRes.getContext().getString(R.string.icon_light));
        int size = 50;
        iconView.setSize(size);
        iconView.setColor(ContextCompat.getColor(AppRes.getContext(), R.color.color_red_light));
        iconView.setDrawingCacheEnabled(true);

        //Bitmap bitmap = Bitmap.createBitmap(iconView.getDrawingCache());
        Bitmap bitmap = loadBitmapFromView(iconView, size, size);
        iconView.setDrawingCacheEnabled(false);

        finalCanvas.drawBitmap(bitmap, positionX, positionY, paint);
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        v.layout(0, 0, width, height);
        v.draw(canvas);
        return bitmap;
    }*/

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
        menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
        menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
        menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');
        menu.add(0, Save, 0, "Save").setShortcut('5', 'z');

        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        paint.setXfermode(null);
        paint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(AppRes.getActivity(), this, paint.getColor()).show();
                return true;
            case EMBOSS_MENU_ID:
                if (paint.getMaskFilter() != mEmboss) {
                    paint.setMaskFilter(mEmboss);
                } else {
                    paint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (paint.getMaskFilter() != mBlur) {
                    paint.setMaskFilter(mBlur);
                } else {
                    paint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                paint.setAlpha(0x80);
                return true;
            case SRCATOP_MENU_ID:

                paint.setXfermode(new PorterDuffXfermode(
                        PorterDuff.Mode.SRC_ATOP));
                paint.setAlpha(0x80);
                return true;
            case Save:
                AlertDialog.Builder editalert = new AlertDialog.Builder(AppRes.getActivity());
                editalert.setTitle("Please Enter the name with which you want to Save");
                final EditText input = new EditText(AppRes.getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT);
                input.setLayoutParams(lp);
                editalert.setView(input);
                editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String name= input.getText().toString();
                        Bitmap bitmap = mv.getDrawingCache();

                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File file = new File("/sdcard/"+name+".png");
                        try
                        {
                            if(!file.exists())
                            {
                                file.createNewFile();
                            }
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                            ostream.close();
                            mv.invalidate();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }finally
                        {

                            mv.setDrawingCacheEnabled(false);
                        }
                    }
                });

                editalert.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
}
