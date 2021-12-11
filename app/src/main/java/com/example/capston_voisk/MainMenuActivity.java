package com.example.capston_voisk;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainMenuActivity extends AppCompatActivity {

    Context cThis_menu;

    // 음성 인식용
    Intent SttIntent_menu;
    SpeechRecognizer mRecognizer_menu;

    // 음성 출력용
    TextToSpeech tts_menu;

    // 화면 처리용
    Button btnSttStart_menu;
    TextView txtInMsg_menu;
    TextView txtSystem_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cThis_menu = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button back = findViewById(R.id.back);
        ImageView menu1_img = findViewById(R.id.menu_img_1);

        back.setOnClickListener(v -> {
            finish();
        });

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        menu1_img.setImageBitmap(bitmap);


        // 음성 인식
        SttIntent_menu = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent_menu.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        SttIntent_menu.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");//한국어 사용
        mRecognizer_menu = SpeechRecognizer.createSpeechRecognizer(cThis_menu);
        mRecognizer_menu.setRecognitionListener(listener_menu);

        // 음성 출력 생성, 리스너 초기화
        tts_menu = new TextToSpeech(cThis_menu, status -> {
            if (status != TextToSpeech.ERROR) {
                tts_menu.setLanguage(Locale.KOREAN);
            }
        });

        // 버튼 설정
        btnSttStart_menu = findViewById(R.id.btn_start_menu);
        btnSttStart_menu.setOnClickListener(view -> {
            System.out.println("음성인식 시작!");
            if (ContextCompat.checkSelfPermission(cThis_menu, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainMenuActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                //권한을 허용하지 않는 경우
            } else {
                //권한을 허용한 경우
                try {
                    mRecognizer_menu.startListening(SttIntent_menu);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
        });

        txtInMsg_menu = findViewById(R.id.txtInMsg_menu);
        txtSystem_menu = findViewById(R.id.txtSystem_menu);
    }

    private RecognitionListener listener_menu = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle_menu) {
            txtSystem_menu.setText("주문을 계속 하시겠습니까?");
        }

        @Override
        public void onBeginningOfSpeech() {
            txtSystem_menu.setText("듣는 중입니다.");
        }

        @Override
        public void onRmsChanged(float v_menu) {

        }

        @Override
        public void onBufferReceived(byte[] bytes_menu) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error_menu) {
            String message_menu;

            switch (error_menu) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message_menu = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message_menu = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message_menu = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message_menu = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message_menu = "네트워크 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message_menu = "찾을 수 없는 단어입니다.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message_menu = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message_menu = "서버 이상";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message_menu = "시간 초과";
                    break;
                default:
                    message_menu = "알 수 없는 오류";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. :" + message_menu, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results_menu) {
            String key = "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results_menu.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            txtInMsg_menu.setText(rs[0]);
            FuncVoiceOrderCheck(rs[0]);
        }

        @Override
        public void onPartialResults(Bundle bundle_menu) {

        }

        @Override
        public void onEvent(int i, Bundle bundle_menu) {

        }
    };

    private void FuncVoiceOrderCheck(String VoiceMsg_menu) {
        if (VoiceMsg_menu.length() < 1) return;

        VoiceMsg_menu = VoiceMsg_menu.replace(" ", "");//공백제거

        if (VoiceMsg_menu.indexOf("예") > -1 || VoiceMsg_menu.indexOf("네") > -1) {
            FuncVoiceOut("원하는 메뉴를 말씀해 주세요.");
            txtSystem_menu.setText("원하는 메뉴를 말씀하세요.");
            finish();
        }
        if (VoiceMsg_menu.indexOf("아니오") > -1 || VoiceMsg_menu.indexOf("아니요") > -1) {
            FuncVoiceOut("결제 화면으로 넘어갑니다.");
            Intent intent = new Intent(this, PayMentActivity.class);
            startActivity(intent);
        }
    }

    private void FuncVoiceOut(String OutMsg) {
        if (OutMsg.length() < 1) return;

        tts_menu.setPitch(1.0f);//목소리 톤1.0
        tts_menu.setSpeechRate(1.0f);//목소리 속도
        tts_menu.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);

        //어플이 종료할때는 완전히 제거
    }
}
