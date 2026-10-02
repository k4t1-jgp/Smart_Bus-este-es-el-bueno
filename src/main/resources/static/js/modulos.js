/* =============================================================
   modulos.js - Aquí se "describe" cada módulo del acta:
   qué campos tiene el formulario, qué columnas lleva la tabla,
   a qué endpoint de la API se conecta y qué acciones tiene.

   app.js lee estas descripciones y arma las pantallas solo,
   así no hay que repetir el mismo HTML trece veces.

   Tipos de campo: texto, numero, dinero, fecha, hora, seleccion,
                   clave (contraseña), area (texto largo), estado (casilla)
   ============================================================= */

const HOY = new Date();

/* ---------- Funciones para llenar las listas desplegables ---------- */
const Opciones = {
  async jornadasPorNombre() {
    const lista = await Api.get("/api/jornadas");
    return lista.filter(j => j.estado).map(j => ({ valor: j.nombre, texto: j.nombre }));
  },
  async jornadas() {
    const lista = await Api.get("/api/jornadas");
    return lista.filter(j => j.estado).map(j => ({
      valor: j.id, texto: `${j.nombre} (${Formato.hora(j.horarioInicio)} - ${Formato.hora(j.horarioFin)})`
    }));
  },
  async estudiantes() {
    const lista = await Api.get("/api/estudiantes");
    return lista.filter(e => e.estado).map(e => ({ valor: e.id, texto: `${e.carne} - ${e.nombre}` }));
  },
  async encargados() {
    const lista = await Api.get("/api/encargados");
    return lista.filter(e => e.estado).map(e => ({ valor: e.id, texto: `${e.nombre} (${e.telefono ?? "sin teléfono"})` }));
  },
  async vehiculos() {
    const lista = await Api.get("/api/vehiculos");
    return lista.filter(v => v.estado).map(v => ({ valor: v.id, texto: `${v.placa} - ${v.marca} ${v.modelo} (${v.capacidad} pasajeros)` }));
  },
  async pilotos() {
    const lista = await Api.get("/api/pilotos");
    return lista.filter(p => p.estado).map(p => ({ valor: p.id, texto: `${p.nombre} - licencia ${p.numeroLicencia}` }));
  },
  async paradas() {
    const lista = await Api.get("/api/paradas");
    return lista.filter(p => p.estado).map(p => ({ valor: p.id, texto: `${p.nombre} (${p.ubicacion})` }));
  },
  async rutasActivas() {
    const lista = await Api.get("/api/rutas");
    return lista.filter(r => r.estado).map(r => ({ valor: r.id, texto: `${r.nombre} - ${r.jornada.nombre}` }));
  },
  // Solo las paradas que pertenecen a la ruta elegida (regla: la parada debe ser de la ruta)
  async paradasDeRuta(rutaId) {
    if (!rutaId) return [];
    const lista = await Api.get("/api/ruta-paradas?rutaId=" + rutaId);
    return lista.map(rp => ({ valor: rp.parada.id, texto: `${rp.orden}. ${rp.parada.nombre}` }));
  },
  async roles() {
    const lista = await Api.get("/api/roles");
    return lista.filter(r => r.estado).map(r => ({ valor: r.id, texto: r.nombre }));
  },
  meses() {
    return Array.from({ length: 12 }, (_, i) => ({ valor: i + 1, texto: Formato.mes(i + 1) }));
  },
  lista(...valores) { return valores.map(v => ({ valor: v, texto: v })); }
};

/* ---------- Insignias de estado ---------- */
function insignia(activo, textoSi = "Activo", textoNo = "Inactivo") {
  return activo
    ? `<span class="insignia si">${textoSi}</span>`
    : `<span class="insignia no">${textoNo}</span>`;
}

const campoEstado = { nombre: "estado", etiqueta: "Activo", tipo: "estado", soloAlEditar: true };

/* =============================================================
   DESCRIPCIÓN DE CADA MÓDULO
   ============================================================= */
const MODULOS = {

  estudiantes: {
    titulo: "Estudiantes",
    singular: "estudiante",
    descripcion: "Registro de los alumnos que usan el servicio de transporte.",
    endpoint: "/api/estudiantes",
    campos: [
      { nombre: "carne", etiqueta: "Carné", obligatorio: true, placeholder: "Ej. 2026-001" },
      { nombre: "nombre", etiqueta: "Nombre completo", obligatorio: true },
      { nombre: "grado", etiqueta: "Grado", obligatorio: true, placeholder: "Ej. 5to Primaria" },
      { nombre: "jornada", etiqueta: "Jornada", tipo: "seleccion", obligatorio: true, cargar: Opciones.jornadasPorNombre },
      { nombre: "direccion", etiqueta: "Dirección", obligatorio: true, ancho: true },
      campoEstado
    ],
    columnas: [
      { titulo: "Carné", valor: e => e.carne },
      { titulo: "Nombre", valor: e => e.nombre },
      { titulo: "Grado", valor: e => e.grado },
      { titulo: "Jornada", valor: e => e.jornada },
      { titulo: "Dirección", valor: e => e.direccion },
      { titulo: "Estado", html: true, clase: "centro", valor: e => insignia(e.estado) }
    ],
    eliminar: { texto: "Desactivar", confirmar: e => `¿Desactivar a ${e.nombre}? No se borra, solo queda inactivo.`, mostrar: e => e.estado }
  },

  encargados: {
    titulo: "Encargados",
    singular: "encargado",
    descripcion: "Padres, madres o responsables de los estudiantes.",
    endpoint: "/api/encargados",
    campos: [
      { nombre: "nombre", etiqueta: "Nombre completo", obligatorio: true },
      { nombre: "telefono", etiqueta: "Teléfono", obligatorio: true, placeholder: "Ej. 44445555" },
      { nombre: "correo", etiqueta: "Correo", placeholder: "Ej. maria@correo.com" },
      { nombre: "direccion", etiqueta: "Dirección", obligatorio: true, ancho: true },
      campoEstado
    ],
    columnas: [
      { titulo: "Nombre", valor: e => e.nombre },
      { titulo: "Teléfono", valor: e => e.telefono },
      { titulo: "Correo", valor: e => e.correo || "—" },
      { titulo: "Dirección", valor: e => e.direccion },
      { titulo: "Estado", html: true, clase: "centro", valor: e => insignia(e.estado) }
    ],
    eliminar: { texto: "Desactivar", confirmar: e => `¿Desactivar a ${e.nombre}?`, mostrar: e => e.estado }
  },

  vinculos: {
    titulo: "Estudiante y encargado",
    singular: "vínculo",
    descripcion: "Une a cada estudiante con sus encargados e indica el parentesco.",
    endpoint: "/api/estudiante-encargados",
    campos: [
      { nombre: "estudianteId", etiqueta: "Estudiante", tipo: "seleccion", obligatorio: true, cargar: Opciones.estudiantes, desde: v => v.estudiante.id },
      { nombre: "encargadoId", etiqueta: "Encargado", tipo: "seleccion", obligatorio: true, cargar: Opciones.encargados, desde: v => v.encargado.id },
      { nombre: "parentesco", etiqueta: "Parentesco", tipo: "seleccion", obligatorio: true,
        cargar: () => Opciones.lista("Padre", "Madre", "Abuelo(a)", "Tío(a)", "Hermano(a)", "Tutor legal", "Otro") },
      { nombre: "tipoEncargado", etiqueta: "Tipo de encargado", tipo: "seleccion", obligatorio: true,
        cargar: () => Opciones.lista("Principal", "Secundario", "Contacto de emergencia") },
      { nombre: "observaciones", etiqueta: "Observaciones", ancho: true }
    ],
    columnas: [
      { titulo: "Estudiante", valor: v => `${v.estudiante.carne} - ${v.estudiante.nombre}` },
      { titulo: "Encargado", valor: v => v.encargado.nombre },
      { titulo: "Parentesco", valor: v => v.parentesco },
      { titulo: "Tipo", valor: v => v.tipoEncargado },
      { titulo: "Teléfono", valor: v => v.encargado.telefono },
      { titulo: "Observaciones", valor: v => v.observaciones || "—" }
    ],
    eliminar: { texto: "Quitar", confirmar: v => `¿Quitar el vínculo entre ${v.estudiante.nombre} y ${v.encargado.nombre}?` }
  },

  vehiculos: {
    titulo: "Vehículos",
    singular: "vehículo",
    descripcion: "Flotilla de buses. Un vehículo en mantenimiento no puede usarse en una ruta activa.",
    endpoint: "/api/vehiculos",
    // Antes de pintar la tabla se piden los disponibles para marcar los que están en mantenimiento
    async preparar() {
      const disponibles = await Api.get("/api/vehiculos/disponibles");
      return { disponibles: new Set(disponibles.map(v => v.id)) };
    },
    campos: [
      { nombre: "placa", etiqueta: "Placa", obligatorio: true, placeholder: "Ej. P-123ABC" },
      { nombre: "marca", etiqueta: "Marca", obligatorio: true, placeholder: "Ej. Toyota" },
      { nombre: "modelo", etiqueta: "Modelo", obligatorio: true, placeholder: "Ej. Coaster" },
      { nombre: "anio", etiqueta: "Año", tipo: "numero", obligatorio: true, min: 1980, max: HOY.getFullYear() + 1 },
      { nombre: "capacidad", etiqueta: "Capacidad (pasajeros)", tipo: "numero", obligatorio: true, min: 1 },
      { nombre: "color", etiqueta: "Color", placeholder: "Ej. Amarillo" },
      { nombre: "verificacion", etiqueta: "Verificación", placeholder: "Ej. Vigente 2026" },
      campoEstado
    ],
    columnas: [
      { titulo: "Placa", valor: v => v.placa },
      { titulo: "Marca", valor: v => v.marca },
      { titulo: "Modelo", valor: v => v.modelo },
      { titulo: "Año", clase: "num", valor: v => v.anio },
      { titulo: "Capacidad", clase: "num", valor: v => v.capacidad },
      { titulo: "Color", valor: v => v.color || "—" },
      { titulo: "Estado", html: true, clase: "centro", valor: (v, ctx) => {
          if (!v.estado) return insignia(false);
          if (!ctx.disponibles.has(v.id)) return `<span class="insignia aviso">En mantenimiento</span>`;
          return insignia(true);
        } }
    ],
    eliminar: { texto: "Desactivar", confirmar: v => `¿Desactivar el vehículo ${v.placa}?`, mostrar: v => v.estado }
  },

  pilotos: {
    titulo: "Pilotos",
    singular: "piloto",
    descripcion: "Conductores de los buses. La licencia debe estar vigente para manejar una ruta activa.",
    endpoint: "/api/pilotos",
    campos: [
      { nombre: "nombre", etiqueta: "Nombre completo", obligatorio: true },
      { nombre: "telefono", etiqueta: "Teléfono", obligatorio: true },
      { nombre: "numeroLicencia", etiqueta: "Número de licencia", obligatorio: true },
      { nombre: "tipoLicencia", etiqueta: "Tipo de licencia", tipo: "seleccion", obligatorio: true, cargar: () => Opciones.lista("A", "B", "C", "M", "E") },
      { nombre: "fechaNacimiento", etiqueta: "Fecha de nacimiento", tipo: "fecha", obligatorio: true },
      { nombre: "fechaVencimientoLicencia", etiqueta: "Vencimiento de licencia", tipo: "fecha", obligatorio: true },
      campoEstado
    ],
    columnas: [
      { titulo: "Nombre", valor: p => p.nombre },
      { titulo: "Teléfono", valor: p => p.telefono },
      { titulo: "Licencia", valor: p => `${p.numeroLicencia} (tipo ${p.tipoLicencia})` },
      { titulo: "Vence", html: true, valor: p => {
          const vencida = !p.fechaVencimientoLicencia || new Date(p.fechaVencimientoLicencia + "T23:59") < HOY;
          return Formato.fecha(p.fechaVencimientoLicencia) + (vencida ? ` <span class="insignia no">Vencida</span>` : "");
        } },
      { titulo: "Estado", html: true, clase: "centro", valor: p => insignia(p.estado) }
    ],
    eliminar: { texto: "Desactivar", confirmar: p => `¿Desactivar al piloto ${p.nombre}?`, mostrar: p => p.estado }
  },

  jornadas: {
    titulo: "Jornadas",
    singular: "jornada",
    descripcion: "Horarios escolares (matutina, vespertina...). Cada ruta pertenece a una jornada.",
    endpoint: "/api/jornadas",
    campos: [
      { nombre: "nombre", etiqueta: "Nombre", obligatorio: true, placeholder: "Ej. Matutina" },
      { nombre: "horarioInicio", etiqueta: "Hora de inicio", tipo: "hora", obligatorio: true },
      { nombre: "horarioFin", etiqueta: "Hora de fin", tipo: "hora", obligatorio: true },
      campoEstado
    ],
    columnas: [
      { titulo: "Nombre", valor: j => j.nombre },
      { titulo: "Inicio", valor: j => Formato.hora(j.horarioInicio) },
      { titulo: "Fin", valor: j => Formato.hora(j.horarioFin) },
      { titulo: "Estado", html: true, clase: "centro", valor: j => insignia(j.estado, "Activa", "Inactiva") }
    ],
    eliminar: { texto: "Desactivar", confirmar: j => `¿Desactivar la jornada ${j.nombre}?`, mostrar: j => j.estado }
  },

  paradas: {
    titulo: "Paradas",
    singular: "parada",
    descripcion: "Puntos donde los estudiantes abordan el bus.",
    endpoint: "/api/paradas",
    campos: [
      { nombre: "nombre", etiqueta: "Nombre", obligatorio: true, placeholder: "Ej. Parque Central" },
      { nombre: "ubicacion", etiqueta: "Ubicación", obligatorio: true, placeholder: "Ej. Frente a la iglesia, Morales" },
      campoEstado
    ],
    columnas: [
      { titulo: "Nombre", valor: p => p.nombre },
      { titulo: "Ubicación", valor: p => p.ubicacion },
      { titulo: "Estado", html: true, clase: "centro", valor: p => insignia(p.estado, "Activa", "Inactiva") }
    ],
    eliminar: { texto: "Desactivar", confirmar: p => `¿Desactivar la parada ${p.nombre}?`, mostrar: p => p.estado }
  },

  rutas: {
    titulo: "Rutas",
    singular: "ruta",
    descripcion: "Una ruta se crea inactiva. Agréguele paradas y luego actívela: al activarla se revisan todas las reglas del acta.",
    endpoint: "/api/rutas",
    campos: [
      { nombre: "nombre", etiqueta: "Nombre", obligatorio: true, placeholder: "Ej. Ruta Norte" },
      { nombre: "recorrido", etiqueta: "Recorrido", placeholder: "Ej. Centro - Colegio" },
      { nombre: "jornadaId", etiqueta: "Jornada", tipo: "seleccion", obligatorio: true, cargar: Opciones.jornadas, desde: r => r.jornada.id },
      { nombre: "horarioSalida", etiqueta: "Hora de salida", tipo: "hora", obligatorio: true },
      { nombre: "horarioLlegada", etiqueta: "Hora de llegada", tipo: "hora", obligatorio: true },
      { nombre: "vehiculoId", etiqueta: "Vehículo", tipo: "seleccion", obligatorio: true, cargar: Opciones.vehiculos, desde: r => r.vehiculo.id },
      { nombre: "pilotoId", etiqueta: "Piloto", tipo: "seleccion", obligatorio: true, cargar: Opciones.pilotos, desde: r => r.piloto.id }
    ],
    columnas: [
      { titulo: "Nombre", valor: r => r.nombre },
      { titulo: "Jornada", valor: r => r.jornada.nombre },
      { titulo: "Horario", valor: r => `${Formato.hora(r.horarioSalida)} a ${Formato.hora(r.horarioLlegada)}` },
      { titulo: "Vehículo", valor: r => `${r.vehiculo.placa} (${r.vehiculo.capacidad})` },
      { titulo: "Piloto", valor: r => r.piloto.nombre },
      { titulo: "Estado", html: true, clase: "centro", valor: r => insignia(r.estado, "Activa", "Inactiva") }
    ],
    accionesExtra: [
      { texto: "Paradas", clase: "neutral", ejecutar: (r) => Paginas.paradasDeRuta(r) },
      { texto: "Activar", clase: "positiva", mostrar: r => !r.estado, editaDatos: true,
        ejecutar: async (r, recargar) => {
          await Api.put(`/api/rutas/${r.id}/activar`);
          avisar(`La ruta ${r.nombre} quedó activa.`);
          recargar();
        } }
    ],
    eliminar: { texto: "Desactivar", confirmar: r => `¿Desactivar la ruta ${r.nombre}? No se borra y conserva su historial.`, mostrar: r => r.estado }
  },

  asignaciones: {
    titulo: "Asignación de estudiantes",
    singular: "asignación",
    descripcion: "Vincula a un estudiante con una ruta activa y la parada donde aborda.",
    endpoint: "/api/asignaciones",
    campos: [
      { nombre: "estudianteId", etiqueta: "Estudiante", tipo: "seleccion", obligatorio: true, cargar: Opciones.estudiantes, desde: a => a.estudiante.id },
      { nombre: "rutaId", etiqueta: "Ruta", tipo: "seleccion", obligatorio: true, cargar: Opciones.rutasActivas, desde: a => a.ruta.id },
      { nombre: "paradaId", etiqueta: "Parada de abordaje", tipo: "seleccion", obligatorio: true,
        dependeDe: "rutaId", cargarCon: Opciones.paradasDeRuta, desde: a => a.parada.id },
      { nombre: "fecha", etiqueta: "Fecha", tipo: "fecha" }
    ],
    columnas: [
      { titulo: "Estudiante", valor: a => `${a.estudiante.carne} - ${a.estudiante.nombre}` },
      { titulo: "Ruta", valor: a => a.ruta.nombre },
      { titulo: "Jornada", valor: a => a.ruta.jornada.nombre },
      { titulo: "Parada", valor: a => a.parada.nombre },
      { titulo: "Fecha", valor: a => Formato.fecha(a.fecha) },
      { titulo: "Estado", html: true, clase: "centro", valor: a => insignia(a.estado, "Activa", "Cancelada") }
    ],
    editable: a => a.estado,
    eliminar: { texto: "Cancelar", confirmar: a => `¿Cancelar la asignación de ${a.estudiante.nombre}?`, mostrar: a => a.estado }
  },

  pagos: {
    titulo: "Pagos",
    singular: "pago",
    descripcion: "Cuotas mensuales de transporte. Solo se permite un pago vigente por estudiante en cada mes y año.",
    endpoint: "/api/pagos",
    campos: [
      { nombre: "estudianteId", etiqueta: "Estudiante", tipo: "seleccion", obligatorio: true, cargar: Opciones.estudiantes, desde: p => p.estudiante.id },
      { nombre: "monto", etiqueta: "Monto (Q)", tipo: "dinero", obligatorio: true },
      { nombre: "mes", etiqueta: "Mes", tipo: "seleccion", obligatorio: true, cargar: async () => Opciones.meses(), inicial: () => HOY.getMonth() + 1 },
      { nombre: "anio", etiqueta: "Año", tipo: "numero", obligatorio: true, min: 2000, inicial: () => HOY.getFullYear() },
      { nombre: "fechaPago", etiqueta: "Fecha de pago", tipo: "fecha" }
    ],
    columnas: [
      { titulo: "Estudiante", valor: p => `${p.estudiante.carne} - ${p.estudiante.nombre}` },
      { titulo: "Periodo", valor: p => `${Formato.mes(p.mes)} ${p.anio}` },
      { titulo: "Monto", clase: "num", valor: p => Formato.dinero(p.monto) },
      { titulo: "Fecha de pago", valor: p => Formato.fecha(p.fechaPago) },
      { titulo: "Estado", html: true, clase: "centro", valor: p => insignia(p.estado, "Vigente", "Anulado") }
    ],
    editable: p => p.estado,
    eliminar: { texto: "Anular", confirmar: p => `¿Anular el pago de ${p.estudiante.nombre} (${Formato.mes(p.mes)} ${p.anio})?`, mostrar: p => p.estado }
  },

  mantenimientos: {
    titulo: "Mantenimiento",
    singular: "mantenimiento",
    descripcion: "Mientras un mantenimiento esté en curso, el vehículo queda bloqueado para rutas activas.",
    endpoint: "/api/mantenimientos",
    campos: [
      { nombre: "vehiculoId", etiqueta: "Vehículo", tipo: "seleccion", obligatorio: true, cargar: Opciones.vehiculos, desde: m => m.vehiculo.id, soloAlCrear: true },
      { nombre: "tipo", etiqueta: "Tipo", tipo: "seleccion", obligatorio: true, cargar: () => Opciones.lista("Preventivo", "Correctivo") },
      { nombre: "costo", etiqueta: "Costo (Q)", tipo: "dinero", obligatorio: true },
      { nombre: "fechaInicio", etiqueta: "Fecha de inicio", tipo: "fecha" },
      { nombre: "detalle", etiqueta: "Detalle del trabajo", tipo: "area", obligatorio: true, ancho: true }
    ],
    columnas: [
      { titulo: "Vehículo", valor: m => `${m.vehiculo.placa} - ${m.vehiculo.marca}` },
      { titulo: "Tipo", valor: m => m.tipo },
      { titulo: "Detalle", valor: m => m.detalle },
      { titulo: "Inicio", valor: m => Formato.fecha(m.fechaInicio) },
      { titulo: "Fin", valor: m => Formato.fecha(m.fechaFin) },
      { titulo: "Costo", clase: "num", valor: m => Formato.dinero(m.costo) },
      { titulo: "Estado", html: true, clase: "centro", valor: m => m.estado ? `<span class="insignia aviso">En curso</span>` : insignia(true, "Finalizado") }
    ],
    editable: m => m.estado,
    accionesExtra: [
      { texto: "Finalizar", clase: "positiva", mostrar: m => m.estado, editaDatos: true,
        ejecutar: async (m, recargar) => {
          if (!confirm(`¿Finalizar el mantenimiento del vehículo ${m.vehiculo.placa}?`)) return;
          await Api.put(`/api/mantenimientos/${m.id}/finalizar`);
          avisar("Mantenimiento finalizado. El vehículo vuelve a estar disponible.");
          recargar();
        } }
    ]
  },

  usuarios: {
    titulo: "Usuarios",
    singular: "usuario",
    descripcion: "Personas que pueden entrar al sistema. La contraseña se guarda encriptada.",
    endpoint: "/api/usuarios",
    campos: [
      { nombre: "nombreUsuario", etiqueta: "Nombre de usuario", obligatorio: true },
      { nombre: "contrasena", etiqueta: "Contraseña", tipo: "clave", obligatorioAlCrear: true,
        placeholder: "Al editar, déjela vacía para no cambiarla", desde: () => "" },
      { nombre: "rolId", etiqueta: "Rol", tipo: "seleccion", obligatorio: true, cargar: Opciones.roles },
      campoEstado
    ],
    columnas: [
      { titulo: "Usuario", valor: u => u.nombreUsuario },
      { titulo: "Rol", valor: u => u.rolNombre },
      { titulo: "Estado", html: true, clase: "centro", valor: u => insignia(u.estado) }
    ],
    eliminar: { texto: "Desactivar", confirmar: u => `¿Desactivar al usuario ${u.nombreUsuario}?`, mostrar: u => u.estado }
  },

  roles: {
    titulo: "Roles",
    singular: "rol",
    descripcion: "Los roles definen qué módulos ve cada usuario en el menú.",
    endpoint: "/api/roles",
    campos: [
      { nombre: "nombre", etiqueta: "Nombre del rol", obligatorio: true },
      { nombre: "descripcion", etiqueta: "Descripción", ancho: true },
      campoEstado
    ],
    columnas: [
      { titulo: "Rol", valor: r => r.nombre },
      { titulo: "Descripción", valor: r => r.descripcion || "—" },
      { titulo: "Estado", html: true, clase: "centro", valor: r => insignia(r.estado) }
    ],
    eliminar: { texto: "Desactivar", confirmar: r => `¿Desactivar el rol ${r.nombre}?`, mostrar: r => r.estado }
  }
};

/* =============================================================
   MENÚ LATERAL (mismo orden que el mapa de navegación)
   ============================================================= */
const ICONOS = {
  inicio: '<path d="M3 11l9-7 9 7M5 10v10h5v-6h4v6h5V10"/>',
  estudiantes: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20c.5-4 3.3-6 6.5-6s6 2 6.5 6M16 4.5a3.5 3.5 0 010 7M18 14c2 .6 3.3 2.6 3.5 6"/>',
  encargados: '<circle cx="12" cy="7" r="3.5"/><path d="M5 21c.5-4.5 3.4-7 7-7s6.5 2.5 7 7"/><path d="M12 14v3"/>',
  vehiculos: '<rect x="4" y="3" width="16" height="15" rx="2.5"/><path d="M4 11h16M8 21v-3M16 21v-3"/><circle cx="8" cy="14.5" r="1"/><circle cx="16" cy="14.5" r="1"/>',
  pilotos: '<circle cx="12" cy="12" r="8.5"/><circle cx="12" cy="12" r="2.5"/><path d="M12 3.5v6M5 15l5-2M19 15l-5-2"/>',
  jornadas: '<circle cx="12" cy="12" r="8.5"/><path d="M12 7v5l3.5 2"/>',
  paradas: '<path d="M12 21s-6.5-6-6.5-11a6.5 6.5 0 0113 0c0 5-6.5 11-6.5 11z"/><circle cx="12" cy="10" r="2.3"/>',
  rutas: '<circle cx="6" cy="18" r="2.2"/><circle cx="18" cy="6" r="2.2"/><path d="M8 18h7a3 3 0 000-6H9a3 3 0 010-6h7"/>',
  asignaciones: '<rect x="5" y="3.5" width="14" height="17" rx="2"/><path d="M9 3.5h6v3H9zM9 12l2 2 4-4"/>',
  pagos: '<rect x="3" y="6" width="18" height="12" rx="2"/><circle cx="12" cy="12" r="2.5"/><path d="M6.5 9.5v5M17.5 9.5v5"/>',
  mantenimiento: '<path d="M14.5 5.5a4 4 0 00-5.2 5.2L4 16l4 4 5.3-5.3a4 4 0 005.2-5.2l-2.6 2.6-2.8-.6-.6-2.8z"/>',
  reportes: '<path d="M4 20h16M7 16v-5M12 16V7M17 16v-8"/>',
  usuarios: '<circle cx="12" cy="8" r="3.5"/><path d="M5 20c.6-4 3.4-6 7-6s6.4 2 7 6"/><path d="M18.5 3.5l1 1.8 2 .3-1.5 1.4.4 2-1.9-1-1.9 1 .4-2-1.5-1.4 2-.3z"/>'
};

const MENU = [
  { clave: "inicio", texto: "Inicio", pagina: "panel" },
  { clave: "estudiantes", texto: "Estudiantes", modulos: ["estudiantes"] },
  { clave: "encargados", texto: "Encargados", modulos: ["encargados", "vinculos"] },
  { clave: "vehiculos", texto: "Vehículos", modulos: ["vehiculos"] },
  { clave: "pilotos", texto: "Pilotos", modulos: ["pilotos"] },
  { clave: "jornadas", texto: "Jornadas", modulos: ["jornadas"] },
  { clave: "paradas", texto: "Paradas", modulos: ["paradas"] },
  { clave: "rutas", texto: "Rutas", modulos: ["rutas"] },
  { clave: "asignaciones", texto: "Asignaciones", modulos: ["asignaciones"] },
  { clave: "pagos", texto: "Pagos", modulos: ["pagos"] },
  { clave: "mantenimiento", texto: "Mantenimiento", modulos: ["mantenimientos"] },
  { clave: "reportes", texto: "Reportes", pagina: "reportes" },
  { clave: "usuarios", texto: "Usuarios y roles", modulos: ["usuarios", "roles"] }
];

/* =============================================================
   PERMISOS POR ROL (actores del acta de constitución)
   Ojo: esto solo esconde opciones del menú. La API todavía no
   bloquea por rol; eso queda como pendiente en el back-end.
   ============================================================= */
const PERMISOS = {
  administrador: { menu: MENU.map(m => m.clave), soloLectura: false },
  coordinador: { menu: ["inicio", "vehiculos", "pilotos", "jornadas", "paradas", "rutas", "asignaciones", "mantenimiento", "reportes"], soloLectura: false },
  pagos: { menu: ["inicio", "pagos", "reportes"], soloLectura: false },
  estudiantes: { menu: ["inicio", "estudiantes", "encargados", "asignaciones", "reportes"], soloLectura: false },
  consulta: { menu: ["inicio", "rutas", "asignaciones", "reportes"], soloLectura: true }
};

// Convierte el nombre del rol que viene de la base de datos en uno de los perfiles de arriba
function perfilDelRol(nombreRol) {
  const n = (nombreRol || "").toLowerCase().normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  if (n.includes("admin")) return "administrador";
  if (n.includes("coordin") || n.includes("transporte")) return "coordinador";
  if (n.includes("pago")) return "pagos";
  if (n.includes("estudiante") || n.includes("encargado")) return "estudiantes";
  return "consulta";
}

// Roles que se crean en el primer uso (los cinco actores del acta)
const ROLES_INICIALES = [
  { nombre: "Administrador", descripcion: "Configura usuarios y catálogos" },
  { nombre: "Coordinador de transportes", descripcion: "Administra rutas, vehículos y pilotos" },
  { nombre: "Encargado de pagos", descripcion: "Registra las cuotas mensuales" },
  { nombre: "Encargado de estudiantes", descripcion: "Registra estudiantes y encargados" },
  { nombre: "Usuario de consulta", descripcion: "Consulta rutas y asignaciones" }
];
