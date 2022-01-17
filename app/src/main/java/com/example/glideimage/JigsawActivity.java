package com.example.glideimage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class JigsawActivity extends AppCompatActivity {
    public static int type = 2;
    public int originPicId;
    public Bitmap originBitmap;
    public Bitmap lastPart;
    public Bitmap blankBitmap;
    public ArrayList<JigsawItem> jigsawItems = new ArrayList<>();
    private JigsawAdapter jigsawAdapter;
    private int count = 0;
    private static int blankId = 0;
    private int lastId = 0;
    private boolean begin = false;
    private int timeCost = 0;
    private Timer timer;
    private TimerTask timerTask;
    TextView timeTxt;
    TextView finishTxt;
    int animationRunning = 0;
    boolean finished = false;
    int[] x;
    int[] y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jigsaw);
        try {
            init();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                timeCost++;
                timeTxt.setText(String.format(getString(R.string.time), (float) timeCost / 10));
            }
        }
    };

    private void startTimer() {
        //防止多次点击开启计时器
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask = null;
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 100);
    }

    private Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);
        int length = Math.min(bm.getHeight(), bm.getWidth());
        return Bitmap.createBitmap(bm, 0, 0, length, length);
    }

    private int getInversions() {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < type * type; i++) {
            for (int j = i + 1; j < type * type; j++)
                if (j != lastId && jigsawItems.get(j).bitmapId < jigsawItems.get(i).bitmapId)
                    inversionCount++;
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }

    private boolean solvable() {
        if (type * type % 2 == 1) {
            return getInversions() % 2 == 0;
        } else {
            if ((lastId / type) % 2 == 1) {
                return getInversions() % 2 == 0;
            } else {
                return getInversions() % 2 == 1;
            }
        }
    }

    private boolean ordered() {
        for (int i = 0; i < lastId; i++)
            if (jigsawItems.get(i).bitmapId != i)
                return false;
        return true;
    }

    private void itemShuffle() {
        finishTxt.setVisibility(View.INVISIBLE);
        JigsawItem blankItem = null;
        for (int i = 0; i < jigsawItems.size(); i++)
            if (jigsawItems.get(i).bitmapId == lastId) {
                blankItem = jigsawItems.get(i);
                jigsawItems.remove(i);
                break;
            }
        Collections.shuffle(jigsawItems);
        while (!solvable() || ordered())
            Collections.shuffle(jigsawItems);
        jigsawItems.add(blankItem);
        blankId = lastId;
        finished = false;
    }

    private void createBitmaps() {
        int itemLength = originBitmap.getHeight() / type;
        Bitmap bitmap;
        JigsawItem jigsawItem;
        for (int i = 0; i < type; i++)
            for (int j = 0; j < type; j++) {
                bitmap = Bitmap.createBitmap(originBitmap, j * itemLength, i * itemLength, itemLength, itemLength);
                jigsawItem = new JigsawItem(i * type + j, bitmap);
                if (i + j == type * 2 - 2)
                    lastPart = bitmap;
                else
                    jigsawItems.add(jigsawItem);
            }
        bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.blank);
        Matrix matrix = new Matrix();
        matrix.postScale((float) itemLength / bitmap.getHeight(), (float) itemLength / bitmap.getWidth());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        blankBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
        jigsawItems.add(new JigsawItem(lastId, blankBitmap));
        blankId = lastId;
        itemShuffle();
    }

    public static int movable(int position) {
        if (position - blankId == type)
            return 1;
        if (blankId - position == type)
            return 2;
        if (blankId / type == position / type)
            if (position - blankId == 1)
                return 3;
            else if (blankId - position == 1)
                return 4;
        return 0;
    }

    private void init() throws FileNotFoundException {
        TextView diffTxt = findViewById(R.id.dif);
        TextView stepTxt = findViewById(R.id.step);
        finishTxt = findViewById(R.id.finish);
        timeTxt = findViewById(R.id.time);
        Button reset = findViewById(R.id.reset), back = findViewById(R.id.back);
        ImageView originPic = findViewById(R.id.originPic);
        Intent intent = getIntent();
        originPicId = intent.getIntExtra("originPic", R.drawable.p1);
        type = intent.getIntExtra("type", 2);
        lastId = type * type - 1;
        x = new int[]{0, 0, -840 / type - 10, 840 / type + 10};
        y = new int[]{-840 / type - 10, 840 / type + 10, 0, 0};
        String[] df = {"2×2 简单", "3×3 正常", "4×4 困难", "5×5 挑战"};

        if (intent.getBooleanExtra("custom", false)) {
            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(intent.getData()));
            int length = Math.min(bit.getHeight(), bit.getWidth());
            if (length > 840) {
                Matrix matrix = new Matrix();
                matrix.postScale((float) 840 / length, (float) 840 / length);
                originBitmap = Bitmap.createBitmap(bit, 0, 0, length, length, matrix, true);
            } else
                originBitmap = Bitmap.createBitmap(bit, 0, 0, length, length);
        } else
            originBitmap = readBitmap(this, originPicId);
        diffTxt.setText(String.format(getString(R.string.difficult), df[type - 2]));
        originPic.setImageBitmap(originBitmap);
        createBitmaps();
        GridView jigsaw = findViewById(R.id.jigsaw);
        jigsaw.setLayoutParams(new LinearLayout.LayoutParams(840 + 10 * (type - 1), 840 + 10 * (type - 1)));
        jigsaw.setNumColumns(type);
        Animation animation = AnimationUtils.loadAnimation(JigsawActivity.this, R.anim.list_anim);
        originPic.setAnimation(animation);
        jigsaw.post(() -> {
            jigsawAdapter = new JigsawAdapter(JigsawActivity.this, jigsawItems, type);
            jigsaw.setAdapter(jigsawAdapter);
            LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation, 0.03f);
            layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
            jigsaw.setLayoutAnimation(layoutAnimationController);
        });
        jigsaw.setSelector(new ColorDrawable(Color.TRANSPARENT));
        back.setOnClickListener(view -> JigsawActivity.this.finish());
        reset.setOnClickListener(view -> {
            itemShuffle();
            jigsawItems.get(lastId).setBitmap(blankBitmap);
            count = 0;
            begin = false;
            if (timer != null)
                timer.cancel();
            timeCost = 0;
            timeTxt.setText(String.format(getString(R.string.time), 0.0));
            stepTxt.setText("步数：0");
            jigsaw.setFocusable(true);
            jigsaw.setEnabled(true);
            jigsawAdapter.notifyDataSetChanged();
        });
        jigsaw.setOnItemClickListener((parent, view, position, id) -> {
            if (!begin) {
                begin = true;
                startTimer();
            }
            int z = movable(position);
            if (z > 0) {

                TranslateAnimation animation1 = new TranslateAnimation(0, x[z - 1], 0, y[z - 1]);
                animation1.setDuration(200);//设置动画持续时间
                animation1.setAnimationListener(new Animation.AnimationListener() {
                    final int bid = blankId;

                    @Override
                    public void onAnimationStart(Animation animation) {
                        animationRunning++;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        animationRunning--;
                        //jigsawAdapter.notifyDataSetChanged();
                        jigsawAdapter.update2(position, jigsaw, blankBitmap);
                        jigsawAdapter.update(bid, jigsaw);
                        //if (finished)
                        //jigsawItems.get(lastId).setBitmap(lastPart);
                        if (animationRunning == 0)
                            jigsawAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                jigsaw.getChildAt(position).startAnimation(animation1);
                Collections.swap(jigsawItems, blankId, position);
                blankId = position;
                count++;
                stepTxt.setText(String.format(getString(R.string.step), count));
                if (ordered()) {
                    finished = true;
                    Toast.makeText(JigsawActivity.this, "拼图完成！", Toast.LENGTH_SHORT).show();
                    jigsaw.setFocusable(false);
                    jigsaw.setEnabled(false);
                    finishTxt.setVisibility(View.VISIBLE);

                    ScaleAnimation animation2 = new ScaleAnimation(2, 1, 2, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    animation2.setDuration(1000);
                    //透明度动画
                    AlphaAnimation animation3 = new AlphaAnimation(0.1f, 1);
                    animation3.setDuration(1000);

                    //装入AnimationSet中
                    AnimationSet set = new AnimationSet(true);
                    set.addAnimation(animation2);
                    set.addAnimation(animation3);
                    finishTxt.startAnimation(set);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        jigsawItems.get(lastId).setBitmap(lastPart);
                        jigsawAdapter.notifyDataSetChanged();
                        TranslateAnimation translateAnimation = new TranslateAnimation(420, 0, 0, 0);
                        translateAnimation.setDuration(600);
                        jigsaw.getChildAt(lastId).startAnimation(translateAnimation);
                    }, 250);

                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jigsawItems.clear();
        if (timer != null) {
            timer.cancel();
        }
    }
}
