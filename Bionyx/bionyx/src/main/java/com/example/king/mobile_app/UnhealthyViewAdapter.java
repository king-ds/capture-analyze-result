package com.example.king.mobile_app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UnhealthyViewAdapter extends AppCompatActivity {

    // Other variables
    private String transactionid, owner, uploaded, image, position, bl, cn, h, sn, yn, tn, stat, diseases, first_name, last_name, email;
    private TextView txtbeaulines, txtclubnails, txtspoonnails, txtterrynails, txtyellownails, txtstatus, txtdiseases;
    private ImageView imagenail;
    private Bitmap bitmap;
    private int counter;
    private SweetAlertDialog pDialog;
    private boolean success;

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
        setContentView(R.layout.unhealthyview);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        counter = 0;
        success = false;

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.first_name = prefs.getString("first_name","");
        this.last_name = prefs.getString("last_name", "");
        this.email = prefs.getString("email", "");
        Intent unhealthy_intent = getIntent();
        // Get the result of transaction id
        transactionid = unhealthy_intent.getStringExtra("transactionid");
        uploaded = unhealthy_intent.getStringExtra("uploaded");
        bl = unhealthy_intent.getStringExtra("BeauLines");
        cn = unhealthy_intent.getStringExtra("ClubbedNails");
        sn = unhealthy_intent.getStringExtra("Spoon");
        tn = unhealthy_intent.getStringExtra("TerryNails");
        yn = unhealthy_intent.getStringExtra("YellowNails");
        stat = unhealthy_intent.getStringExtra("status");
        diseases = unhealthy_intent.getStringExtra("diseases");
        image = unhealthy_intent.getStringExtra("image");
        bitmap = unhealthy_intent.getParcelableExtra("bitmapImage");

        txtbeaulines = findViewById(R.id.tvBL);
        txtclubnails = findViewById(R.id.tvCN);
        txtspoonnails = findViewById(R.id.tvSN);
        txtterrynails = findViewById(R.id.tvTN);
        txtyellownails = findViewById(R.id.tvYN);
        txtstatus = findViewById(R.id.tvStatus);
        txtdiseases = findViewById(R.id.tvDiseases);
        imagenail = findViewById(R.id.image);

        setResult();

        getSupportActionBar().setTitle("Transaction #: "+transactionid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void setResult(){

        txtdiseases.setText(diseases);
        txtbeaulines.setText(df.format(Float.parseFloat(bl)));
        txtclubnails.setText(df.format(Float.parseFloat(cn)));
        txtspoonnails.setText(df.format(Float.parseFloat(sn)));
        txtterrynails.setText(df.format(Float.parseFloat(tn)));
        txtyellownails.setText(df.format(Float.parseFloat(yn)));
        txtstatus.setText(stat);
        historyPhotoLoader.DisplayImage(image, imagenail);
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
                    final SweetAlertDialog dialog = new SweetAlertDialog(UnhealthyViewAdapter.this, SweetAlertDialog.NORMAL_TYPE);
                    dialog.setTitleText("Confirmation")
                            .setContentText("Do you want to generate it as PDF?")
                            .setConfirmText("Save")
                            .setCancelText("Don't save")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                    pDialog = new SweetAlertDialog(UnhealthyViewAdapter.this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
                                    pDialog.show();
                                    pDialog.setCancelable(false);
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            generatePDF();
                                            if (success) {
                                                pDialog.dismiss();
                                                UnhealthyViewAdapter.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new SweetAlertDialog(UnhealthyViewAdapter.this, SweetAlertDialog.SUCCESS_TYPE)
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
                                            }else{
                                                new SweetAlertDialog(UnhealthyViewAdapter.this, SweetAlertDialog.ERROR_TYPE)
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
                                    Handler handler = new Handler(){
                                        public void handleMessage(android.os.Message msg){
                                            pDialog.dismiss();

                                        };
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

                }else {
                    UnhealthyViewAdapter.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SweetAlertDialog(UnhealthyViewAdapter.this, SweetAlertDialog.WARNING_TYPE)
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

}