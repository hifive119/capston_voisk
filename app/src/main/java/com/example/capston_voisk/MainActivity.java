package com.example.capston_voisk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Context cThis;

    // 음성 인식용
    Intent SttIntent;
    SpeechRecognizer mRecognizer;

    // 음성 출력용
    TextToSpeech tts;

    // 화면 처리용
    Button btnSttStart;
    TextView txtInMsg;
    TextView txtSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cThis = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 음성 인식
        SttIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");   // 한국어 사용
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);

        // 음성 출력 생성, 리스너 초기화
        tts = new TextToSpeech(cThis, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.KOREAN);
            }
        });

        // 버튼 설정
        btnSttStart = findViewById(R.id.btn_start);
        btnSttStart.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                // 권한을 허용하지 않는 경우
            } else {
                // 권한을 허용한 경우
                try {
                    mRecognizer.startListening(SttIntent);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        txtInMsg = findViewById(R.id.txtInMsg);
        txtSystem = findViewById(R.id.txtSystem);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            txtSystem.setText("원하는 메뉴를 말씀해 주세요");
        }

        @Override
        public void onBeginningOfSpeech() {
            txtSystem.setText("듣는 중입니다.");
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없는 단어입니다.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 이상";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "시간 초과";
                    break;
                default:
                    message = "알 수 없는 오류";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. :" + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            //txtInMsg.setText(rs[0] + "\n" + txtInMsg.getText());
            txtInMsg.setText(rs[0]);
            FuncVoiceOrderCheck(rs[0]);
            //mRecognizer.startListening(SttIntent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    private void FuncVoiceOrderCheck (String VoiceMsg) {

        Button bulgogi_button = findViewById(R.id.menu_bulgogi_btn);
        ImageView menu1_img = findViewById(R.id.menu_bulgogi);

        Button chicken_button = findViewById(R.id.menu_chicken_btn);
        ImageView menu2_img = findViewById(R.id.menu_chicken);

        Button shrimp_button = findViewById(R.id.menu_shrimp_btn);
        ImageView menu3_img = findViewById(R.id.menu_shrimp);

        Button deri_button = findViewById(R.id.menu_deri_btn);
        ImageView menu4_img = findViewById(R.id.menu_deri);

        Button cheese_button = findViewById(R.id.menu_cheese_btn);
        ImageView menu5_img = findViewById(R.id.menu_cheese);

        if (VoiceMsg.length() < 1) return;

        VoiceMsg = VoiceMsg.replace(" ", ""); // 공백 제거

        bulgogi_button.setOnClickListener(v-> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable)menu1_img.getDrawable()).getBitmap();
            float scale = (1024/(float)bitmap.getWidth());
            int image_w = (int) (bitmap.getWidth() * scale);
            int image_h = (int) (bitmap.getHeight() * scale);
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent_menu = new Intent(this, MainMenuActivity.class);
            intent_menu.putExtra("image", byteArray);
            startActivity(intent_menu);
        });

        chicken_button.setOnClickListener(v-> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable)menu2_img.getDrawable()).getBitmap();
            float scale = (1024/(float)bitmap.getWidth());
            int image_w = (int) (bitmap.getWidth() * scale);
            int image_h = (int) (bitmap.getHeight() * scale);
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent_menu = new Intent(this, MainMenuActivity.class);
            intent_menu.putExtra("image", byteArray);
            startActivity(intent_menu);
        });

        shrimp_button.setOnClickListener(v-> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable)menu3_img.getDrawable()).getBitmap();
            float scale = (1024/(float)bitmap.getWidth());
            int image_w = (int) (bitmap.getWidth() * scale);
            int image_h = (int) (bitmap.getHeight() * scale);
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent_menu = new Intent(this, MainMenuActivity.class);
            intent_menu.putExtra("image", byteArray);
            startActivity(intent_menu);
        });

        deri_button.setOnClickListener(v-> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable)menu4_img.getDrawable()).getBitmap();
            float scale = (1024/(float)bitmap.getWidth());
            int image_w = (int) (bitmap.getWidth() * scale);
            int image_h = (int) (bitmap.getHeight() * scale);
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent_menu = new Intent(this, MainMenuActivity.class);
            intent_menu.putExtra("image", byteArray);
            startActivity(intent_menu);
        });

        cheese_button.setOnClickListener(v-> {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = ((BitmapDrawable)menu5_img.getDrawable()).getBitmap();
            float scale = (1024/(float)bitmap.getWidth());
            int image_w = (int) (bitmap.getWidth() * scale);
            int image_h = (int) (bitmap.getHeight() * scale);
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
            resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent_menu = new Intent(this, MainMenuActivity.class);
            intent_menu.putExtra("image", byteArray);
            startActivity(intent_menu);
        });

        if (VoiceMsg.indexOf("불고기버거") > -1 || VoiceMsg.indexOf("불고기") > -1) {
            FuncVoiceOut("불고기 버거를 주문 하셨습니다.");
            bulgogi_button.performClick();
        }
        if (VoiceMsg.indexOf("치킨버거") > -1 || VoiceMsg.indexOf("치킨") > -1) {
            FuncVoiceOut("치킨 버거를 주문 하셨습니다.");
            chicken_button.performClick();
        }
        if (VoiceMsg.indexOf("새우버거") > -1 || VoiceMsg.indexOf("새우") > -1) {
            FuncVoiceOut("새우 버거를 주문 하셨습니다.");
            shrimp_button.performClick();
        }
        if (VoiceMsg.indexOf("데리버거") > -1 || VoiceMsg.indexOf("데리") > -1) {
            FuncVoiceOut("데리 버거를 주문 하셨습니다.");
            deri_button.performClick();
        }
        if (VoiceMsg.indexOf("치즈버거") > -1 || VoiceMsg.indexOf("치즈") > -1) {
            FuncVoiceOut("치즈 버거를 주문 하셨습니다.");
            cheese_button.performClick();
        }
    }

    private void FuncVoiceOut (String OutMsg) {
        if (OutMsg.length() < 1) return;

        tts.setPitch(1.0f);
        tts.setSpeechRate(1.0f);
        tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
    }

}