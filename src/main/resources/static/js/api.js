/* =============================================================
   api.js - Todo lo que habla con la API de Spring Boot pasa por aquí.

   Si la página se abre desde Spring (http://localhost:8080) las
   peticiones van al mismo servidor. Si se abre desde otro lado
   (por ejemplo Live Server en el puerto 5500) se envían a
   http://localhost:8080 y para eso existe la clase CorsConfig.
   ============================================================= */

const API_BASE = (location.protocol.startsWith("http") && location.port === "8080")
  ? ""
  : "http://localhost:8080";

/**
 * Hace una petición a la API y devuelve el JSON de respuesta.
 * Si la API responde con error (400, 404, 409...) lanza un Error con
 * el "mensaje" que armó ManejadorGlobalExcepciones.
 */
async function api(metodo, ruta, cuerpo) {
  const opciones = { method: metodo, headers: {} };
  if (cuerpo !== undefined) {
    opciones.headers["Content-Type"] = "application/json";
    opciones.body = JSON.stringify(cuerpo);
  }

  let respuesta;
  try {
    respuesta = await fetch(API_BASE + ruta, opciones);
  } catch (e) {
    throw new Error("No hay conexión con la API en " + (API_BASE || location.origin)
      + ". Revise que SmartbusApiApplication esté corriendo y que MySQL esté encendido.");
  }

  const texto = await respuesta.text();
  let datos = null;
  if (texto) {
    try { datos = JSON.parse(texto); } catch (e) { datos = texto; }
  }

  if (!respuesta.ok) {
    const mensaje = (datos && datos.mensaje) ? datos.mensaje
      : "La API respondió con el error " + respuesta.status;
    throw new Error(mensaje);
  }
  return datos;
}

// Atajos para no escribir el método cada vez
const Api = {
  get: (ruta) => api("GET", ruta),
  post: (ruta, cuerpo) => api("POST", ruta, cuerpo),
  put: (ruta, cuerpo) => api("PUT", ruta, cuerpo ?? {}),
  delete: (ruta) => api("DELETE", ruta)
};

/* ---------- Pequeñas ayudas de formato ---------- */
const Formato = {
  // "2026-09-01" -> "01/09/2026"
  fecha(valor) {
    if (!valor) return "—";
    const [a, m, d] = String(valor).split("-");
    return d ? `${d}/${m}/${a}` : valor;
  },
  // "06:00:00" -> "06:00"
  hora(valor) { return valor ? String(valor).substring(0, 5) : "—"; },
  dinero(valor) {
    if (valor === null || valor === undefined) return "—";
    return "Q " + Number(valor).toLocaleString("es-GT", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  },
  mes(numero) {
    const meses = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio",
      "agosto", "septiembre", "octubre", "noviembre", "diciembre"];
    return meses[numero - 1] ?? numero;
  },
  // Evita que un texto con < o > rompa el HTML
  seguro(texto) {
    return String(texto ?? "").replace(/[&<>"']/g, c =>
      ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
  }
};
