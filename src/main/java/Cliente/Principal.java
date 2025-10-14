package Cliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Principal {
    private static ClienteATM clienteActual = null;

    public static void main(String[] args) {
        ClienteGUI interfaz = new ClienteGUI();
        interfaz.setVisible(true);

        // Listener para botón Conectar
        interfaz.registrarConexion(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String servidor = interfaz.txtServidor.getText().trim();
                String puertoStr = interfaz.txtPuerto.getText().trim();

                if (servidor.isEmpty() || puertoStr.isEmpty()) {
                    interfaz.mostrarError("Debe ingresar servidor y puerto");
                    return;
                }

                try {
                    int puerto = Integer.parseInt(puertoStr);

                    // Crear y arrancar el cliente
                    clienteActual = new ClienteATM(interfaz, servidor, puerto);
                    interfaz.setCliente(clienteActual);
                    clienteActual.start();

                } catch (NumberFormatException ex) {
                    interfaz.mostrarError("El puerto debe ser un número válido");
                }
            }
        });

        // Listener para botón Desconectar
        interfaz.registrarDesconexion(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (clienteActual != null) {
                    clienteActual.desconectar();
                    clienteActual = null;
                }
            }
        });

        // Listener para botón Retirar
        interfaz.registrarRetiro(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String codigo = interfaz.getCodigoCliente();
                String montoStr = interfaz.getMontoTexto();

                if (codigo.isEmpty() || montoStr.isEmpty()) {
                    interfaz.mostrarError("Debe ingresar código y monto");
                    return;
                }

                try {
                    double monto = Double.parseDouble(montoStr);

                    if (monto <= 0) {
                        interfaz.mostrarError("El monto debe ser mayor a cero");
                        return;
                    }

                    if (clienteActual == null || !clienteActual.isConectado()) {
                        interfaz.mostrarError("No hay conexión activa");
                        return;
                    }

                    clienteActual.enviarRetiro(codigo, monto);
                    interfaz.limpiarCampos();

                } catch (NumberFormatException ex) {
                    interfaz.mostrarError("El monto debe ser un número válido");
                }
            }
        });
    }
}