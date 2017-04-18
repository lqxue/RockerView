package com.zcsj.rockerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static com.zcsj.rockerview.RockerView.DirectionMode.DIRECTION_8;

public class MainActivity extends AppCompatActivity {

    private RockerView mRockerView;
    private TextView mTvShake;
    private TextView mTvAngle;
    private TextView mTvModel;
    private TextView mTvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRockerView = (RockerView) findViewById(R.id.rocker);
        mTvAngle = (TextView) findViewById(R.id.tv_angle);
        mTvModel = (TextView) findViewById(R.id.tv_model);
        mTvShake = (TextView) findViewById(R.id.tv_shake);
        mTvSpeed = (TextView) findViewById(R.id.tv_speed);
        init();
        final CircleQuickenView mCircleQuickenView = (CircleQuickenView) findViewById(R.id.press_rocker);
        mCircleQuickenView.setOnPressChangeListener(new CircleQuickenView.OnPressChangeListener() {
            @Override
            public void onPress() {
                mTvSpeed.setText("加速");
            }

            @Override
            public void onFinish() {
                mTvSpeed.setText("匀速");
            }
        });
        mTvModel.setText("当前模式：方向有改变时回调；8个方向");
        mRockerView.setOnShakeListener(DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void direction(RockerView.Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {
                    mTvShake.setText("当前方向：中心");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {
                    mTvShake.setText("当前方向：下");
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {
                    mTvShake.setText("当前方向：左");
                } else if (direction == RockerView.Direction.DIRECTION_UP) {
                    mTvShake.setText("当前方向：上");
                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    mTvShake.setText("当前方向：右");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_LEFT) {
                    mTvShake.setText("当前方向：左下");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_RIGHT) {
                    mTvShake.setText("当前方向：右下");
                } else if (direction == RockerView.Direction.DIRECTION_UP_LEFT) {
                    mTvShake.setText("当前方向：左上");
                } else if (direction == RockerView.Direction.DIRECTION_UP_RIGHT) {
                    mTvShake.setText("当前方向：右上");
                }
            }

            @Override
            public void onFinish() {
            }
        });
        mRockerView.setOnAngleChangeListener(new RockerView.OnAngleChangeListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void angle(double angle) {
                mTvAngle.setText("当前角度：" + angle);
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private void init() {
        mTvShake.setText("当前方向：右");
        mTvAngle.setText("当前角度：" + 0);
        mTvSpeed.setText("匀速");
    }
}
