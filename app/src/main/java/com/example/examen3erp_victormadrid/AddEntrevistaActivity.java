package com.example.examen3erp_victormadrid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.examen3erp_victormadrid.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddEntrevistaActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_AUDIO_RECORD = 2;

    private EditText descripcionEditText, periodistaEditText;
    private TextInputEditText fechaEditText;
    private Button fechaButton;
    private ImageButton audioButton;
    private ImageView imagenImageView;
    private Uri imagenUri;
    private byte[] imagenBytes, audioBytes;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entrevista);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        descripcionEditText = findViewById(R.id.descriptionET);
        periodistaEditText = findViewById(R.id.periodistaET);
        fechaEditText = findViewById(R.id.fechaET);
        imagenImageView = findViewById(R.id.imageButton);
        audioButton = findViewById(R.id.audioButton);
        MaterialButton addEntrevista = findViewById(R.id.addEntrevista);

        addEntrevista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> entrevista = new HashMap<>();
                entrevista.put("descripcion", Objects.requireNonNull(descripcionEditText.getText()).toString());
                entrevista.put("periodista", Objects.requireNonNull(periodistaEditText.getText()).toString());
                entrevista.put("fecha", Objects.requireNonNull(fechaEditText.getText()).toString());
                entrevista.put("imagen", imagenBytes);
                entrevista.put("audio", audioBytes);

                db.collection("entrevistas").add(entrevista).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AddEntrevistaActivity.this, "Entrevista agregada con éxito", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddEntrevistaActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEntrevistaActivity.this, "Error al agregar entrevista", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        imagenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageOptions();
            }
        });

        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioOptions();
            }
        });
    }

    private void showImageOptions() {
        // Opciones para seleccionar imagen o tomar una foto
        CharSequence[] options = {"Elegir de Galería", "Tomar Foto"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Imagen");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Seleccionar imagen de la galería
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                } else if (which == 1) {
                    // Verificar permisos antes de abrir la cámara
                    if (ContextCompat.checkSelfPermission(AddEntrevistaActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddEntrevistaActivity.this,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    } else {
                        // Permiso ya concedido, abrir la cámara
                        openCamera();
                    }
                }
            }
        });

        builder.show();
    }

    private void showAudioOptions() {
        // Opciones para seleccionar audio o grabar uno nuevo
        CharSequence[] options = {"Elegir de Galería", "Grabar Audio"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Audio");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Seleccionar audio de la galería
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (which == 1) {
                    // Verificar permisos antes de grabar audio
                    if (ContextCompat.checkSelfPermission(AddEntrevistaActivity.this,
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddEntrevistaActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
                    } else {
                        // Permiso ya concedido, grabar audio
                        recordAudio();
                    }
                }
            }
        });

        builder.show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void recordAudio() {
        Intent recordAudioIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (recordAudioIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(recordAudioIntent, REQUEST_AUDIO_RECORD);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, abrir la cámara
                openCamera();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, grabar audio
                recordAudio();
            } else {
                // Permiso denegado
                Toast.makeText(this, "Permiso de grabación de audio denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imagenUri = data.getData();
            imagenImageView.setImageURI(imagenUri);

            try {
                Bitmap imagenBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imagenBytes = stream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            try {
                audioBytes = getBytesFromUri(audioUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null) {
            // Foto tomada con la cámara
            Bitmap imagenBitmap = (Bitmap) data.getExtras().get("data");
            imagenImageView.setImageBitmap(imagenBitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imagenBytes = stream.toByteArray();
        } else if (requestCode == 4 && resultCode == RESULT_OK && data != null) {
            // Audio grabado con el micrófono
            Uri audioUri = data.getData();
            try {
                audioBytes = getBytesFromUri(audioUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        InputStream inputStream = getContentResolver().openInputStream(uri);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            stream.write(buffer, 0, bytesRead);
        }
        return stream.toByteArray();
    }
}
