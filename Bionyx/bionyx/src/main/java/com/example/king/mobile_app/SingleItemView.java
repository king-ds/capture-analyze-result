package com.example.king.mobile_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
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

public class SingleItemView extends Activity {
    // Declare Variables
    private Button GeneratePDF;
    private static ProgressDialog mProgressDialog;
    public String transactionid, owner, uploaded, image, position, bl, cn, h, sh, yn, tn, stat, diseases, first_name, last_name, email, filtered_image;
    public Bitmap bitmap;
    ImageLoader imageLoader = new ImageLoader(this);
    DecimalFormat df = new DecimalFormat("#0.00");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.singleitemview);

        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        this.first_name = prefs.getString("first_name","");
        this.last_name = prefs.getString("last_name", "");
        this.email = prefs.getString("email", "");
        Intent i = getIntent();
        // Get the result of transaction id
        transactionid = i.getStringExtra("transactionid");
        // Get the result of owner
        owner = i.getStringExtra("owner");
        // Get the result of uploaded
        uploaded = i.getStringExtra("uploaded");
        // Get the percentage of result
        bl = i.getStringExtra("BeauLines");
        cn = i.getStringExtra("ClubbedNails");
        h = i.getStringExtra("Healthy");
        sh = i.getStringExtra("Splinter");
        tn = i.getStringExtra("TerryNails");
        yn = i.getStringExtra("YellowNails");
        //Get the status
        stat = i.getStringExtra("status");
        //Get the diseases
        diseases = i.getStringExtra("diseases");
        //Get the filtered image
        filtered_image = i.getStringExtra("filtered_image");
        // Get the captuted of image
        image = i.getStringExtra("image");
        bitmap = (Bitmap) i.getParcelableExtra("bitmapImage");

        // Locate the TextViews in singleitemview.xml
        GeneratePDF = (Button)findViewById(R.id.btnGeneratePDF);
//        TextView txttransactionid = (TextView) findViewById(R.id.transactionid);
//        TextView txtowner = (TextView) findViewById(R.id.owner);
//        TextView txtuploaded = (TextView) findViewById(R.id.uploaded);
        TextView txtbl = (TextView) findViewById(R.id.tvBL);
        TextView txtcn = (TextView) findViewById(R.id.tvCN);
        TextView txth = (TextView) findViewById(R.id.tvH);
        TextView txtsh = (TextView) findViewById(R.id.tvSH);
        TextView txttn = (TextView) findViewById(R.id.tvTN);
        TextView txtyn = (TextView) findViewById(R.id.tvYN);
        TextView txtdiseases = (TextView) findViewById(R.id.tvDiseases);
        TextView txtstatus = (TextView)findViewById(R.id.tvStatus);



        // Locate the ImageView in singleitemview.xml
        ImageView imgNail = (ImageView) findViewById(R.id.image);

        GeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(SingleItemView.this);
                //Set Progress dialog title
                mProgressDialog.setTitle("Generate PDF");
                //Set progress dialog message
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setCanceledOnTouchOutside(false);
                //Show progress dialog
                mProgressDialog.show();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        generatePDF();
                    }
                });
                thread.start();

                Handler handler = new Handler(){
                    public void handleMessage(android.os.Message msg){
                        mProgressDialog.dismiss();

                    };
                };

            }
        });

        // Set text the following results to the TextViews
//        txttransactionid.setText(transactionid);
//        //Owner
//        txtowner.setText(owner);
//        //Uploaded date time
//        txtuploaded.setText(uploaded);
//        Beau Lines
        txtbl.setText(df.format(Float.parseFloat(bl)));
        //Clubbed Nails
        txtcn.setText(df.format(Float.parseFloat(cn)));
        //Healthy
        txth.setText(df.format(Float.parseFloat(h)));
        //Splinter Hemorrhage
        txtsh.setText(df.format(Float.parseFloat(sh)));
        //Terry's Nails
        txttn.setText(df.format(Float.parseFloat(tn)));
        //Yellow Nail Syndrome
        txtyn.setText(df.format(Float.parseFloat(yn)));
        //Diseases
        txtdiseases.setText(diseases);
        //Status
        txtstatus.setText(stat);

        // Capture position and set results to the ImageView
        // Passes flag images URL into ImageLoader.class
        imageLoader.DisplayImage(filtered_image, imgNail);
    }
    private void generatePDF(){

        Rectangle pagesize = PageSize.LETTER;
        Document document = new Document(pagesize);
        Date date = new Date();
        String strDataFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDataFormat);
        final String formattedDate = dateFormat.format(date);
        boolean success;
        success = false;
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

            String dest = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +last_name +"_"+formattedDate+ ".pdf";
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

            PdfPCell healthyLbl = new PdfPCell(new Phrase("Healthy",font));
            healthyLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(healthyLbl);

            PdfPCell healthyScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(h)),font));
            healthyScore.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(healthyScore);

            PdfPCell splintLbl = new PdfPCell(new Phrase("Spoon Nails",font));
            splintLbl.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(splintLbl);

            PdfPCell splintScore = new PdfPCell(new Phrase(df.format(Float.parseFloat(sh)),font));
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

        if(success==true){
            mProgressDialog.dismiss();
            SingleItemView.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SingleItemView.this, last_name+"_"+formattedDate+".pdf is generated", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            mProgressDialog.dismiss();
            SingleItemView.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SingleItemView.this, "Failed to generate", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}