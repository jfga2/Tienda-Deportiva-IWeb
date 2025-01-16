package JaySports.service;

import JaySports.model.*;
import JaySports.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CarritoService carritoService;

    @Transactional
    public Pedido crearPedidoDesdeCarrito(Usuario usuario, Carrito carrito) {
        // Verificar si ya existe un pedido pendiente
        if (pedidoRepository.findPendingByUsuario(usuario).isPresent()) {
            throw new IllegalStateException("Ya existe un pedido pendiente para este usuario");
        }

        // Crear nuevo pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(carrito.getPrecioTotal());
        pedido.setNumeroPedido(generarNumeroPedido());

        // Crear ProductoPedido para cada ProductoCarrito
        List<ProductoPedido> productosPedido = new ArrayList<>();
        for (ProductoCarrito pc : carrito.getProductosCarrito()) {
            ProductoPedido pp = new ProductoPedido();
            pp.setPedido(pedido);
            pp.setProducto(pc.getProducto());
            pp.setCantidad(pc.getCantidad());
            pp.setPrecioUnitario(pc.getPrecioUnitario());
            pp.setSubtotal(pc.getSubtotal());
            productosPedido.add(pp);
        }
        pedido.setProductosPedido(productosPedido);

        // Guardar el pedido
        pedido = pedidoRepository.save(pedido);

        // Vaciar el carrito
        carritoService.vaciarCarrito(carrito);

        return pedido;
    }

    private String generarNumeroPedido() {
        return "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}