package com.example.ep_francoavaro_4_11_2025_u3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class SensorFragment extends Fragment implements SensorEventListener
{
    private TextView accX, accY, accZ;
    private TextView gyroX, gyroY, gyroZ;
    private TextView tipoMov;
    private TextView resumen;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private final ArrayList<Float> historialX = new ArrayList<>();
    private int conteoBruscos = 0;
    private int bruscosConsecutivos = 0;
    private boolean ultimoFueBrusco = false;
    private static final int MAX_HISTORIAL = 10;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accX = view.findViewById(R.id.xcoor);
        accY = view.findViewById(R.id.ycoor);
        accZ = view.findViewById(R.id.zcoor);
        gyroX = view.findViewById(R.id.gyro_x);
        gyroY = view.findViewById(R.id.gyro_y);
        gyroZ = view.findViewById(R.id.gyro_z);
        tipoMov = view.findViewById(R.id.tipo_mov);
        resumen = view.findViewById(R.id.resumen);

        sensorManager = (SensorManager) requireActivity().getSystemService(requireActivity().SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (accelerometer == null) {
            tipoMov.setText("No hay acelerómetro");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            accX.setText(String.format("X: %.2f", x));
            accY.setText(String.format("Y: %.2f", y));
            accZ.setText(String.format("Z: %.2f", z));

            if (historialX.size() >= MAX_HISTORIAL) {
                historialX.remove(0);
            }
            historialX.add(x);

            String tipo;
            if (x >= -2 && x <= 2) {
                tipo = "Suave";
                ultimoFueBrusco = false;
            } else if ((x > 2 && x <= 5) || (x >= -5 && x < -2)) {
                tipo = "Moderado";
                ultimoFueBrusco = false;
            } else {
                tipo = "Brusco";
                conteoBruscos++;
                if (ultimoFueBrusco) {
                    bruscosConsecutivos++;
                } else {
                    bruscosConsecutivos = 1;
                }
                ultimoFueBrusco = true;
            }
            tipoMov.setText("Movimiento: " + tipo);

            if (bruscosConsecutivos >= 3) {
                if (getActivity() != null) {
                    ((MainActivity) getActivity()).moverMarcadorPorBruscos();
                }
            }

            if (!historialX.isEmpty()) {
                float promedio = 0;
                for (float v : historialX) promedio += v;
                promedio /= historialX.size();

                float max = Collections.max(historialX);
                float min = Collections.min(historialX);

                resumen.setText(
                        "Promedio X: " + String.format("%.2f", promedio) + "\n" +
                        "Máx: " + String.format("%.2f", max) + "\n" +
                        "Mín: " + String.format("%.2f", min) + "\n" +
                        "Bruscos totales: " + conteoBruscos
                );
            }

        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            gyroX.setText(String.format("X: %.2f", x));
            gyroY.setText(String.format("Y: %.2f", y));
            gyroZ.setText(String.format("Z: %.2f", z));
        }
    }
}
