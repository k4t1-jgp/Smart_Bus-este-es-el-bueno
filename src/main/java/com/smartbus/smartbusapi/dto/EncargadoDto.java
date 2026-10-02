package com.smartbus.smartbusapi.dto;

/**
 * DTO ("Data Transfer Object") de Encargado.
 *
 * Es la "forma" del JSON que llega en un POST o PUT. No es la entidad: aquí las relaciones se
 * reciben solo como el ID del objeto relacionado (por ejemplo "rolId": 1), y el servicio se
 * encarga de buscar el objeto completo en la base de datos.
 */
public class EncargadoDto {

    private String nombre;

    private String telefono;

    private String correo;

    private String direccion;

    private Boolean estado;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}
