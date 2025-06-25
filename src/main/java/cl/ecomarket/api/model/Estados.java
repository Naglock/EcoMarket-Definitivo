package cl.ecomarket.api.model;

public enum Estados {
    PENDIENTE,  // significa que el pedido está pendiente de ser procesado
    RECHAZADA, // significa que el pedido ha sido rechazado por ventas
    APROBADA, // significa que el pedido ha sido aprobado por ventas
    EN_PREPARACION, // significa que el pedido está siendo preparado por logistica
    EN_CAMINO, // significa que el pedido está en camino a ser entregado
    ENTREGADO, // significa que el pedido ha sido entregado al cliente
}
