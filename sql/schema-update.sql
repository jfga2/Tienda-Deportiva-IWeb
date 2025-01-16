DO $$
BEGIN
    -- Crear tabla pedido si no existe
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'pedido') THEN
CREATE TABLE pedido (
                        id SERIAL PRIMARY KEY,
                        numero_pedido VARCHAR(255) UNIQUE NOT NULL,
                        fecha_pedido TIMESTAMP NOT NULL,
                        estado VARCHAR(255) NOT NULL,
                        total NUMERIC(10, 2) NOT NULL,
                        tipo_entrega VARCHAR(255),
                        detalles TEXT,
                        id_usuario BIGINT NOT NULL,
                        FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);
END IF;

    -- Crear tabla producto_pedido si no existe
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'producto_pedido') THEN
CREATE TABLE producto_pedido (
                                 id SERIAL PRIMARY KEY,
                                 id_pedido BIGINT NOT NULL,
                                 id_producto BIGINT NOT NULL,
                                 cantidad INTEGER NOT NULL,
                                 precio_unitario NUMERIC(10, 2) NOT NULL,
                                 subtotal NUMERIC(10, 2) NOT NULL,
                                 FOREIGN KEY (id_pedido) REFERENCES pedido(id) ON DELETE CASCADE,
                                 FOREIGN KEY (id_producto) REFERENCES productos(id) ON DELETE CASCADE
);
END IF;
END $$;
