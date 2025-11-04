package com.example.ep_francoavaro_4_11_2025_u3;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback
{
    private GoogleMap mMap;
    private Marker marcador;
    private LatLng UBICACION_BASE = new LatLng(-17.78629, -63.18117);
    private LatLng UBICACION_ALTERNATIVA = new LatLng(-17.85, -63.20);

    private boolean yaSeMovioPorBruscos = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        crearMarcador(UBICACION_BASE, "Estable", BitmapDescriptorFactory.HUE_GREEN);
    }

    private void crearMarcador(LatLng posicion, String titulo, float color) {
        if (marcador != null) {
            marcador.remove();
        }
        marcador = mMap.addMarker(new MarkerOptions()
                .position(posicion)
                .title(titulo)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13f));

        if (posicion.latitude < -17.8) {
            Toast.makeText(getContext(), "Zona de riesgo", Toast.LENGTH_LONG).show();
        }
    }

    public void moverMarcadorPorBruscos() {
        if (!yaSeMovioPorBruscos) {
            yaSeMovioPorBruscos = true;
            crearMarcador(UBICACION_ALTERNATIVA, "¡Movimiento brusco!", BitmapDescriptorFactory.HUE_RED);
        } else {
            crearMarcador(UBICACION_ALTERNATIVA, "¡Otro movimiento brusco!", BitmapDescriptorFactory.HUE_RED);
        }
    }
}
