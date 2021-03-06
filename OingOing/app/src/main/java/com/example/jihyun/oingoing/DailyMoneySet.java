package com.example.jihyun.oingoing;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.example.jihyun.oingoing.R.id.setButton;


public class DailyMoneySet extends AppCompatActivity implements View.OnClickListener{


    private Realm mRealm;


    private Button setbutton;
    private DatePickerDialog startDatePickerDialog;
    private DatePickerDialog endDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    private EditText startDate, endDate, setMoney;
    private EditText Aim; // 목표

    private AlertDialog.Builder subDialog;//입력 다 안했을때 뜨는 다이얼로그
    private AlertDialog.Builder dateErrorDialog;//시작날짜>종료날짜 일떄 뜨는 다이얼로그
    private AlertDialog.Builder overlap;//날짜가 겹치는 다이얼 로그가 있을 때 뜨는 다이얼로그



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailymoneyset);

        mRealm = Realm.getInstance(DailyMoneySet.this);

        dateFormatter = new SimpleDateFormat("yyyy-M-d", Locale.KOREA);
        startDate = (EditText) findViewById(R.id.startdate);
        endDate = (EditText) findViewById(R.id.enddate);
        setMoney = (EditText) findViewById(R.id.setMoney);
        Aim = (EditText)findViewById(R.id.setAim);

        startDate.setInputType(InputType.TYPE_NULL);
        startDate.requestFocus();
        endDate.setInputType(InputType.TYPE_NULL);
        startDate.setOnClickListener(this);
        endDate.setOnClickListener(this);
        Calendar newCalendar = Calendar.getInstance();
        startDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                startDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                endDate.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));





    }
    //설정버튼 이벤트
    public void btnSet(View v){

        //입력 다 안했을 때 뜨는 다이얼로그
        subDialog = new AlertDialog.Builder(DailyMoneySet.this)
                .setMessage("모두 입력해주세요")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg2, int which) {
                        dlg2.cancel();
                    }
                });


        dateErrorDialog = new AlertDialog.Builder(DailyMoneySet.this)
                .setMessage("종료날짜를 다시 설정해 주세요")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg2, int which) {
                        dlg2.cancel();
                    }
                });

        overlap = new AlertDialog.Builder(DailyMoneySet.this)
                .setMessage("겹치는 날짜가 존재합니다")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg2, int which) {
                        dlg2.cancel();
                    }
                });





        if(!Utility.isBlankField(setMoney) && !Utility.isBlankField(Aim)
                && !Utility.isBlankField(startDate) && !Utility.isBlankField(endDate)) {
            //금액 가져오기
            String daily_money = setMoney.getText().toString();
            String AimName = Aim.getText().toString();
            int dailymoney= Integer.parseInt(daily_money);
            //String start_date=startDate.getText().toString();
            //String end_date=endDate.getText().toString();



            //시작날짜 종료날찌 확인
            try {
                Date d_s = new SimpleDateFormat("yyyy-M-d").parse(startDate.getText().toString());
                Date d_e = new SimpleDateFormat("yyyy-M-d").parse(endDate.getText().toString());
                //Log.e("ee", d.toString()+"날짜 date변");
                if(d_s.compareTo(d_e)>=0){

                    dateErrorDialog.show();
                }
                else{


                    //중복방지
                    int count=0; //중복방지 변수
                    RealmResults<DailyMoneyModel> results = mRealm.where(DailyMoneyModel.class)
                            .findAll();
                    mRealm.beginTransaction();
                    for(int i=0;i<results.size();i++){

                        if(results.get(i).getstartDate().compareTo(d_s)<=0 &&            //resualts.startdate < d_s < resualts.EndDate
                                d_s.compareTo(results.get(i).getEndDate())<=0){
                            count++;
                            break;
                        }else if(results.get(i).getstartDate().compareTo(d_e)<=0 &&      //resualts.startdate < d_e < resualts.EndDate
                                d_e.compareTo(results.get(i).getEndDate())<=0){
                            count++;
                            break;
                        }else if (d_s.compareTo(results.get(i).getstartDate())<=0 &&     //d_s<resualts.startdate<d_e
                                results.get(i).getstartDate().compareTo(d_e)<=0){
                            count++;
                            break;

                        }else if(d_s.compareTo(results.get(i).getEndDate())<=0 &&        //d_s<resualts.Enddate<d_e
                                results.get(i).getEndDate().compareTo(d_e)<=0){
                            count++;
                            break;
                        }


                    }
                    mRealm.commitTransaction();


                    if(count>0){
                        overlap.show();
                    }
                    else{

                        //데이터베이스에 추가하기
                        mRealm.beginTransaction();

                        DailyMoneyModel DM = mRealm.createObject(DailyMoneyModel.class);
                        DM.setAimName(AimName);
                        DM.setMoney_set(dailymoney);
                        DM.setStartDate(d_s);
                        DM.setEndDate(d_e);

                        mRealm.commitTransaction();

                        Log.d("ee", AimName +"  " +dailymoney+"  ");


                        //메인으로 돌아가기
                        Intent intent = new Intent(getApplicationContext(),//현재화면의
                                MainActivity.class);//다음 넘어갈 클래스 지정

                        startActivity(intent);//다음 화면으로 넘어간다
                        finish();


                    }


                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }
        else{

            subDialog.show();

        }

    }


    @Override
    public void onClick(View view) {
        if(view == startDate) {
            startDatePickerDialog.show();
        } else if(view == endDate) {
            endDatePickerDialog.show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
