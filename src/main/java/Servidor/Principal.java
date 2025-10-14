package Servidor;

public class Principal {
    public static void main(String[] args) {
        BancoGUI interfaz = new BancoGUI();
        interfaz.setVisible(true);

        // Registrar listener para botón Iniciar
        interfaz.registrarEventoIniciar(e -> {
            String puertoStr = interfaz.txtPuerto.getText().trim();
            if (puertoStr.isEmpty()) {
                interfaz.mostrarError("Debe ingresar un puerto");
                return;
            }

            try {
                int puerto = Integer.parseInt(puertoStr);

                // Crear y arrancar el servidor solo cuando se presione el botón
                Servidor servidor = new Servidor(interfaz, puerto);
                servidor.start();

                interfaz.btnIniciar.setEnabled(false);
                interfaz.txtPuerto.setEnabled(false);

            } catch (NumberFormatException ex) {
                interfaz.mostrarError("El puerto debe ser un número válido");
            }
        });
    }
}
