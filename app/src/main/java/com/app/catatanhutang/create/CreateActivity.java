package com.app.catatanhutang.create;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.catatanhutang.R;
import com.app.catatanhutang.home.HomeActivity;
import com.app.catatanhutang.utils.database.DaoHandler;
import com.app.catatanhutang.utils.database.DaoSession;
import com.app.catatanhutang.utils.database.TblPengeluaran;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.paperdb.Paper;

public class CreateActivity extends AppCompatActivity {

    @BindView(R.id.etPembelian)
    EditText etPembelian;
    @BindView(R.id.etNominal)
    EditText etNominal;
    @BindView(R.id.btnSimpan)
    Button btnSimpan;

    private Unbinder unbinder;
    private DaoSession daoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        Paper.init(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Buat Daftar Hutang");

        unbinder = ButterKnife.bind(this);
        daoSession = DaoHandler.getInstance(this);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pembelian = etPembelian.getText().toString();
                String nominal = etNominal.getText().toString();

                if (pembelian.isEmpty() || nominal.isEmpty()){
                    Toast.makeText(CreateActivity.this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    showNotif();

                    Paper.book().write("hutang",etPembelian.getText().toString());
                    Paper.book().write("nominal",etNominal.getText().toString());

                    /*
                    Fungsi untuk menambahkan data kedalam database. Disini kita menambahkan data
                    kedalam tabel TblPengeluaran.
                    Di Greendao jika mau menambakan data fungsi yang kita panggil adalah insert.
                     */
                    TblPengeluaran tblPengeluaran = new TblPengeluaran();
                    tblPengeluaran.setPengeluaran(pembelian);
                    tblPengeluaran.setNominal(Integer.parseInt(nominal));
                    daoSession.getTblPengeluaranDao().insert(tblPengeluaran);

                    Toast.makeText(CreateActivity.this, "Berhasil menginput data hutang",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CreateActivity.this, HomeActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void showNotif() {
        NotificationManager notificationManager;

        Intent mIntent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fromnotif", "notif");
        mIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setColor(getResources().getColor(R.color.colorAccent));
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_monetization_on_black_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_monetization_on_black_24dp))
                .setTicker("notif starting")
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Catatan Hutang")
                .setContentText("Hutang Anda Bertambah");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(115, builder.build());
    }
}
