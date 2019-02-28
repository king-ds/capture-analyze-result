package com.example.king.mobile_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.king.mobile_app.BaseActivity.currentIp;

public class SpoonViewAdapter extends AppCompatActivity {

    // Other variables
    private int counter;
    private boolean success;

    // String variables
    private String transactionid, is_answered, disorder, owner, uploaded, image, position, bl, cn, h, sn, yn, tn, stat, diseases, first_name, last_name, email;
    private String A1, A2, A3, A4, A5, A6, A7, A8;

    // GUI staff
    private TextView txtstatus, txtdiseases;
    private TextView answer_1, answer_2, answer_3, answer_4, answer_5, answer_6, answer_7, answer_8;
    private TextView final_answer_1, final_answer_2, final_answer_3, final_answer_4, final_answer_5, final_answer_6, final_answer_7, final_answer_8;
    private Button okay;
    private ImageView imagenail;
    private Bitmap bitmap;
    private SweetAlertDialog pDialog;
    private View focusView;

    // URL Server
    private String UPDATE_ANSWER = "http://" + currentIp + "/api/updateAnswer/";

    // Image loader
    HistoryPhotoLoader historyPhotoLoader = new HistoryPhotoLoader(this);

    //Decimal format
    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from unhealthyviewxml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.spoonview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        counter = 0;
        success = false;

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.first_name = prefs.getString("first_name","");
        this.last_name = prefs.getString("last_name", "");
        this.email = prefs.getString("email", "");

        Intent spoon_intent = getIntent();
        // Get the result of transaction id
        transactionid = spoon_intent.getStringExtra("transactionid");
        uploaded = spoon_intent.getStringExtra("uploaded");
        stat = spoon_intent.getStringExtra("status");
        disorder = spoon_intent.getStringExtra("disorder");
        diseases = spoon_intent.getStringExtra("diseases");
        image = spoon_intent.getStringExtra("image");

        A1 = spoon_intent.getStringExtra("answer_1");
        A2 = spoon_intent.getStringExtra("answer_2");
        A3 = spoon_intent.getStringExtra("answer_3");
        A4 = spoon_intent.getStringExtra("answer_4");
        A5 = spoon_intent.getStringExtra("answer_5");
        A6 = spoon_intent.getStringExtra("answer_6");
        A7 = spoon_intent.getStringExtra("answer_7");
        A8 = spoon_intent.getStringExtra("answer_8");
        is_answered = spoon_intent.getStringExtra("isAnswered");

        bitmap = spoon_intent.getParcelableExtra("bitmapImage");

        UPDATE_ANSWER+= transactionid+"/";

        final_answer_1 = findViewById(R.id.tvA1);
        final_answer_2 = findViewById(R.id.tvA2);
        final_answer_3 = findViewById(R.id.tvA3);
        final_answer_4 = findViewById(R.id.tvA4);
        final_answer_5 = findViewById(R.id.tvA5);
        final_answer_6 = findViewById(R.id.tvA6);
        final_answer_7 = findViewById(R.id.tvA7);
        final_answer_8 = findViewById(R.id.tvA8);

        txtstatus = findViewById(R.id.tvStatus);
        txtdiseases = findViewById(R.id.tvDiseases);
        imagenail = findViewById(R.id.image);

        setDetails();
        if(is_answered.equals("true")) {
            setAnswer();
        }

        getSupportActionBar().setTitle("Transaction #: "+transactionid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setDetails(){

        txtdiseases.setText(diseases);
        txtstatus.setText(stat);
        historyPhotoLoader.DisplayImage(image, imagenail);
    }

    private void setAnswer(){

        final_answer_1.setText(A1);
        final_answer_2.setText(A2);
        final_answer_3.setText(A3);
        final_answer_4.setText(A4);
        final_answer_5.setText(A5);
        final_answer_6.setText(A6);
        final_answer_7.setText(A7);
        final_answer_8.setText(A8);
    }

    private void generatePDF(){

        Rectangle pagesize = PageSize.LETTER;
        Document document = new Document(pagesize);
        Date date = new Date();
        String strDataFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDataFormat);
        final String formattedDate = dateFormat.format(date);

        try {

            //PDF CREATION
            //logo
            Resources res = this.getResources();
            Drawable drawable = res.getDrawable(R.drawable.logo_bionyx);
            BitmapDrawable bitDw = ((BitmapDrawable)drawable);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image logo = Image.getInstance(stream.toByteArray());
            logo.scaleAbsolute(173,61);
            logo.setAbsolutePosition(220f,700f);

            String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bionyx/" +last_name +"_"+formattedDate+ ".pdf";
            System.out.println(dest);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
            //font types
            Font header = new Font(Font.FontFamily.COURIER, 22, Font.BOLD);
            Font info = new Font(Font.FontFamily.COURIER,14,Font.BOLD);
            Font font = new Font(Font.FontFamily.COURIER,12);
            Paragraph space = new Paragraph();
            for (int i = 0; i < 4; i++){
                space.add(new Paragraph(" "));
            }
            document.open();
            document.add(logo);
            document.add(space);
            //contents (name)
            Paragraph name = new Paragraph("Name: "+last_name+", "+first_name, info);
            document.add(name);

            Paragraph emails = new Paragraph("Email: "+email, info);
            document.add(emails);
            //label result
            Paragraph resultsLbl = new Paragraph("ASSESSMENT RESULTS", header);
            resultsLbl.setAlignment(Element.ALIGN_CENTER);
            document.add(space);
            document.add(resultsLbl);

            Paragraph imgLbl = new Paragraph("Captured Image",info);
            imgLbl.setAlignment(Element.ALIGN_CENTER);
            document.add(space);
            document.add(space);
            document.add(imgLbl);
            //captured image
            System.out.println(image);
            Image captured = Image.getInstance(new URL(image));
            captured.rotate();
            captured.scaleAbsolute(160f,100f);
            captured.setAbsolutePosition(220f,405f);
            document.add(captured);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(80);
            table.setSpacingAfter(30f);
            table.setSpacingBefore(30f);
            float[] columnWidths  = {3f,2f};
            table.setWidths(columnWidths);
            //table headers
            PdfPCell disorderHead = new PdfPCell(new Phrase("Type of Disorder",info));
            disorderHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(disorderHead);
            PdfPCell percentageHead = new PdfPCell(new Phrase("Percentage",info));
            percentageHead.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(percentageHead);

            //actual results
            PdfPCell beauLbl = new PdfPCell(new Phrase("Beau Lines",font));
            beauLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(beauLbl);

            PdfPCell beauScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(bl)),font));
            beauScore.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(beauScore);

            PdfPCell clubLbl = new PdfPCell(new Phrase("Clubbing",font));
            clubLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(clubLbl);

            PdfPCell clubScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(cn)),font));
            clubScore.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(clubScore);

            PdfPCell splintLbl = new PdfPCell(new Phrase("Spoon Nails",font));
            splintLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(splintLbl);

            PdfPCell splintScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(sn)),font));
            splintScore.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(splintScore);

            PdfPCell terryLbl = new PdfPCell(new Phrase("Terry's Nails",font));
            terryLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(terryLbl);

            PdfPCell terryScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(tn)),font));
            terryScore.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(terryScore);

            PdfPCell yellowLbl = new PdfPCell(new Phrase("Yellow Nail Syndrome",font));
            yellowLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(yellowLbl);

            PdfPCell yellowScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(yn)),font));
            yellowScore.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(yellowScore);

            document.add(table);
            document.add(new Paragraph("Diseases: "));
            Paragraph deas = new Paragraph(diseases);
            String[] parts = diseases.split("-");
            int count = parts.length;
            for(int i = 0; i<count;i++)
                document.add(new Paragraph(parts[i]));

            document.add(new Paragraph("Status: "));
            document.add(new Paragraph(stat));
            String note = "** All results of the possible diseases analyzed by the system are the predicted diseases , based on the fingernail features. **";
            Paragraph reminder = new Paragraph(note);
            reminder.setAlignment(Element.ALIGN_CENTER);
            document.add(reminder);

            document.close();
            success = true;
        } catch (DocumentException e) {
            e.printStackTrace();
            success = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
    }

    /*
    Action bar
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.history_item, menu);
        return true;
    }

    /*
  Boolean for selected options in menu
   */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.save_item:
                if(counter == 0) {
                    if(is_answered.equals("false")) {
                        openSpoonNailsQuestionnaire();
                    }else {

                        final SweetAlertDialog dialog = new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.NORMAL_TYPE);
                        dialog.setTitleText("Confirmation")
                                .setContentText("Do you want to generate it as PDF?")
                                .setConfirmText("Save")
                                .setCancelText("Don't save")
                                .showCancelButton(true)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                        pDialog = new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
                                        pDialog.show();
                                        pDialog.setCancelable(false);
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                generatePDF();
                                                if (success) {
                                                    pDialog.dismiss();
                                                    SpoonViewAdapter.this.runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.SUCCESS_TYPE)
                                                                    .setTitleText("Awesome!")
                                                                    .setContentText("Your transaction has been saved as PDF")
                                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                        @Override
                                                                        public void onClick(SweetAlertDialog sDialog) {
                                                                            sDialog.dismiss();
                                                                            counter = +1;
                                                                        }
                                                                    })
                                                                    .show();
                                                        }
                                                    });
                                                } else {
                                                    new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Oh snap!")
                                                            .setContentText("An error has occured while generating a PDF")
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sDialog) {
                                                                    sDialog.dismiss();
                                                                    counter = +1;
                                                                }
                                                            })
                                                            .show();
                                                }
                                            }
                                        });
                                        thread.start();
                                        Handler handler = new Handler() {
                                            public void handleMessage(android.os.Message msg) {
                                                pDialog.dismiss();

                                            }

                                            ;
                                        };
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                }else {
                    SpoonViewAdapter.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("You have already generated the PDF!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetErrors(){

        answer_1.setError(null);
        answer_2.setError(null);
        answer_3.setError(null);
        answer_4.setError(null);
        answer_5.setError(null);
        answer_6.setError(null);
        answer_7.setError(null);
        answer_8.setError(null);
    }

    private void openSpoonNailsQuestionnaire(){
        AlertDialog.Builder spoon_questionnaire = new AlertDialog.Builder(SpoonViewAdapter.this);
        View SpoonQuestionView = getLayoutInflater().inflate(R.layout.spoon_nails_questionnaire, null);
        spoon_questionnaire.setView(SpoonQuestionView);
        final AlertDialog view_spoon_questionnaire = spoon_questionnaire.create();
        view_spoon_questionnaire.show();

        answer_1 = view_spoon_questionnaire.findViewById(R.id.etAnswer1);
        answer_2 = view_spoon_questionnaire.findViewById(R.id.etAnswer2);
        answer_3 = view_spoon_questionnaire.findViewById(R.id.etAnswer3);
        answer_4 = view_spoon_questionnaire.findViewById(R.id.etAnswer4);
        answer_5 = view_spoon_questionnaire.findViewById(R.id.etAnswer5);
        answer_6 = view_spoon_questionnaire.findViewById(R.id.etAnswer6);
        answer_7 = view_spoon_questionnaire.findViewById(R.id.etAnswer7);
        answer_8 = view_spoon_questionnaire.findViewById(R.id.etAnswer8);
        okay = view_spoon_questionnaire.findViewById(R.id.btnSubmit);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String A1 = answer_1.getText().toString();
                String A2 = answer_2.getText().toString();
                String A3 = answer_3.getText().toString();
                String A4 = answer_4.getText().toString();
                String A5 = answer_5.getText().toString();
                String A6 = answer_6.getText().toString();
                String A7 = answer_7.getText().toString();
                String A8 = answer_8.getText().toString();

                boolean cancel = false;
                resetErrors();

                if (TextUtils.isEmpty(A1)) {
                    answer_1.setError("This field cannot be blank");
                    focusView = answer_1;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A2)) {
                    answer_2.setError("This field cannot be blank");
                    focusView = answer_2;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A3)) {
                    answer_3.setError("This field cannot be blank");
                    focusView = answer_3;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A4)) {
                    answer_4.setError("This field cannot be blank");
                    focusView = answer_4;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A5)) {
                    answer_5.setError("This field cannot be blank");
                    focusView = answer_5;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A6)) {
                    answer_6.setError("This field cannot be blank");
                    focusView = answer_6;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A7)) {
                    answer_7.setError("This field cannot be blank");
                    focusView = answer_7;
                    cancel = true;
                }

                else if (TextUtils.isEmpty(A8)) {
                    answer_8.setError("This field cannot be blank");
                    focusView = answer_8;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    new UpdateQuestionnaireAnswer().execute();
                    view_spoon_questionnaire.dismiss();
                }
            }
        });
    }

    private class UpdateQuestionnaireAnswer extends AsyncTask<Void, Void, Void> {

        boolean isUpdated = false;
        int response_code = 0;
        String response_message;
        final String A1 = answer_1.getText().toString();
        final String A2 = answer_2.getText().toString();
        final String A3 = answer_3.getText().toString();
        final String A4 = answer_4.getText().toString();
        final String A5 = answer_5.getText().toString();
        final String A6 = answer_6.getText().toString();
        final String A7 = answer_7.getText().toString();
        final String A8 = answer_8.getText().toString();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Sending");
            pDialog.show();
            pDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            JSONfunctions parser = new JSONfunctions();
            JSONObject update = parser.getSpoonAnswerObject(true, A1, A2, A3, A4, A5, A6, A7, A8);
            String message = update.toString();
            System.out.println(message);

            try {
                URL myUrl = new URL(UPDATE_ANSWER);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod("PUT");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(message.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");

                OutputStream os = new BufferedOutputStream(connection.getOutputStream());
                System.out.println(os);
                os.write(message.getBytes());
                os.flush();
                os.close();

                //Connect to url
                connection.connect();
                response_code = connection.getResponseCode();
                response_message = connection.getResponseMessage();
                if (response_code == 200) {
                    isUpdated = true;
                    Log.e("UserProfileActivity", "Server response message : " + response_message);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String message;

            if(isUpdated){

                pDialog.dismiss();
                SpoonViewAdapter.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Awesome!")
                                .setContentText("You have submitted the answer")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        final_answer_1.setText(answer_1.getText());
                                        final_answer_2.setText(answer_2.getText());
                                        final_answer_3.setText(answer_3.getText());
                                        final_answer_4.setText(answer_4.getText());
                                        final_answer_5.setText(answer_5.getText());
                                        final_answer_6.setText(answer_6.getText());
                                        final_answer_7.setText(answer_7.getText());
                                        final_answer_8.setText(answer_8.getText());
                                        is_answered = "true";
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });

            }else{

                pDialog.dismiss();
                SpoonViewAdapter.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new SweetAlertDialog(SpoonViewAdapter.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error!")
                                .setContentText("Please check your internet connection")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });
            }
        }
    }
}