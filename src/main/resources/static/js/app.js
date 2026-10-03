/* =============================================================
   app.js - Arma las pantallas del sistema.

   1. Sesión: login, primer administrador y cerrar sesión.
   2. Menú lateral según el rol del usuario.
   3. Navegación con el "#" de la URL (ej. index.html#/rutas).
   4. Pantalla CRUD genérica (formulario + tabla) para cada módulo.
   5. Pantallas especiales: panel de inicio, reportes y paradas de una ruta.
   ============================================================= */

const $ = (selector, raiz = document) => raiz.querySelector(selector);
const esc = Formato.seguro;

let usuario = null;   // usuario que inició sesión
let permisos = null;  // qué puede ver según su rol

/* ---------- Avisos emergentes ---------- */
function avisar(mensaje, tipo = "ok") {
  const div = document.createElement("div");
  div.className = "aviso" + (tipo === "error" ? " error" : "");
  div.textContent = mensaje;
  $("#avisos").appendChild(div);
  setTimeout(() => div.remove(), tipo === "error" ? 7000 : 4000);
}

// Ejecuta una acción y, si la API responde error, muestra el mensaje
async function intentar(accion) {
  try { return await accion(); } catch (e) { avisar(e.message, "error"); }
}

/* =============================================================
   1. SESIÓN
   ============================================================= */
function iniciar() {
  const guardado = sessionStorage.getItem("smartbus-usuario");
  if (guardado) {
    entrar(JSON.parse(guardado));
  } else {
    mostrarLogin();
  }
}

async function mostrarLogin() {
  $("#app").hidden = true;
  $("#pantalla-login").hidden = false;
  $("#form-login [name=nombreUsuario]").focus();
  // Si todavía no hay usuarios, se ofrece crear el primer administrador
  try {
    const usuarios = await Api.get("/api/usuarios");
    $("#form-primer-admin").hidden = usuarios.length > 0;
  } catch (e) {
    $("#login-error").textContent = e.message;
  }
}

$("#form-login").addEventListener("submit", async (evento) => {
  evento.preventDefault();
  const datos = Object.fromEntries(new FormData(evento.target));
  $("#login-error").textContent = "";
  try {
    const u = await Api.post("/api/auth/login", datos);
    sessionStorage.setItem("smartbus-usuario", JSON.stringify(u));
    evento.target.reset();
    entrar(u);
  } catch (e) {
    $("#login-error").textContent = e.message;
  }
});

$("#form-primer-admin").addEventListener("submit", async (evento) => {
  evento.preventDefault();
  const datos = Object.fromEntries(new FormData(evento.target));
  await intentar(async () => {
    // Se crean los roles del acta que falten
    const existentes = await Api.get("/api/roles");
    for (const rol of ROLES_INICIALES) {
      if (!existentes.some(r => r.nombre === rol.nombre)) {
        existentes.push(await Api.post("/api/roles", rol));
      }
    }
    const admin = existentes.find(r => r.nombre === "Administrador");
    await Api.post("/api/usuarios", { nombreUsuario: datos.nombreUsuario, contrasena: datos.contrasena, rolId: admin.id });
    const u = await Api.post("/api/auth/login", datos);
    sessionStorage.setItem("smartbus-usuario", JSON.stringify(u));
    avisar("Administrador creado. Bienvenido.");
    entrar(u);
  });
});

function entrar(u) {
  usuario = u;
  permisos = PERMISOS[perfilDelRol(u.rolNombre)];
  $("#pantalla-login").hidden = true;
  $("#app").hidden = false;
  $("#usuario-nombre").textContent = u.nombreUsuario;
  $("#usuario-rol").textContent = u.rolNombre;
  $("#avatar").textContent = u.nombreUsuario.charAt(0).toUpperCase();
  pintarMenu();
  navegar();
}

$("#btn-salir").addEventListener("click", () => {
  sessionStorage.removeItem("smartbus-usuario");
  usuario = null;
  location.hash = "";
  mostrarLogin();
});

$("#btn-menu").addEventListener("click", () => $("#menu-lateral").classList.toggle("abierto"));

/* =============================================================
   2. MENÚ
   ============================================================= */
function pintarMenu() {
  $("#lista-menu").innerHTML = MENU
      .filter(opcion => permisos.menu.includes(opcion.clave))
      .map(opcion => `
      <li><a href="#/${opcion.clave}" data-clave="${opcion.clave}">
        <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${ICONOS[opcion.clave]}</svg>
        ${opcion.texto}
      </a></li>`).join("");
}

/* =============================================================
   3. NAVEGACIÓN
   ============================================================= */
window.addEventListener("hashchange", navegar);

function navegar() {
  if (!usuario) return;
  const [clave, sub] = location.hash.replace(/^#\/?/, "").split("/");
  let opcion = MENU.find(m => m.clave === clave);
  if (!opcion || !permisos.menu.includes(opcion.clave)) opcion = MENU[0];

  document.querySelectorAll("#lista-menu a").forEach(a =>
      a.classList.toggle("activo", a.dataset.clave === opcion.clave));
  $("#menu-lateral").classList.remove("abierto");

  const contenido = $("#contenido");
  contenido.innerHTML = "";
  contenido.focus();

  if (opcion.pagina === "panel") return Paginas.panel(contenido);
  if (opcion.pagina === "reportes") return Paginas.reportes(contenido);

  const claveModulo = opcion.modulos.includes(sub) ? sub : opcion.modulos[0];
  pantallaCrud(contenido, claveModulo, opcion);
}

/* =============================================================
   4. PANTALLA CRUD GENÉRICA
   ============================================================= */
async function pantallaCrud(contenedor, clave, opcionMenu) {
  const modulo = MODULOS[clave];
  const soloLectura = permisos.soloLectura;
  const estado = { lista: [], ctx: {}, filtro: "" };

  // Pestañas (por ejemplo Usuarios | Roles)
  const pestanas = opcionMenu.modulos.length > 1
      ? `<nav class="pestanas">${opcionMenu.modulos.map(m =>
          `<a href="#/${opcionMenu.clave}/${m}" class="${m === clave ? "activo" : ""}">${MODULOS[m].titulo}</a>`).join("")}</nav>`
      : "";

  contenedor.innerHTML = `
    ${pestanas}
    <div class="encabezado">
      <div>
        <h1>${modulo.titulo}</h1>
        <p>${modulo.descripcion}</p>
      </div>
      <div class="acciones">
        ${soloLectura ? "" : `<button class="btn btn-primario" id="btn-nuevo">+ Nuevo ${modulo.singular}</button>`}
      </div>
    </div>

    <section class="tarjeta" id="tarjeta-form" hidden>
      <h2 id="titulo-form"></h2>
      <form id="form-crud" novalidate>
        <div class="rejilla-form" id="campos"></div>
        <div class="botonera">
          <button type="submit" class="btn btn-guardar">Guardar</button>
          <button type="button" class="btn btn-gris" id="btn-cancelar">Cancelar</button>
        </div>
      </form>
    </section>

    <section class="tarjeta">
      <div class="tarjeta-cabecera">
        <h2>Listado de ${modulo.titulo.toLowerCase()}</h2>
        <div class="derecha">
          <label class="buscador">
            <svg viewBox="0 0 24 24" width="17" height="17" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/></svg>
            <input type="search" id="buscar" placeholder="Buscar..." aria-label="Buscar">
          </label>
        </div>
      </div>
      <div class="contenedor-tabla">
        <table>
          <thead><tr>
            ${modulo.columnas.map(c => `<th class="${c.clase || ""}">${c.titulo}</th>`).join("")}
            <th class="centro">Acciones</th>
          </tr></thead>
          <tbody id="filas"><tr><td class="vacio" colspan="99">Cargando...</td></tr></tbody>
        </table>
      </div>
      <p class="pie-tabla" id="pie"></p>
    </section>

    <div id="panel-extra"></div>`;

  const tarjetaForm = $("#tarjeta-form", contenedor);
  const form = $("#form-crud", contenedor);
  let editando = null;

  /* ----- Cargar y pintar la tabla ----- */
  async function recargar() {
    try {
      const [lista, ctx] = await Promise.all([
        Api.get(modulo.endpoint),
        modulo.preparar ? modulo.preparar() : {}
      ]);
      estado.lista = lista;
      estado.ctx = ctx;
      pintarFilas();
    } catch (e) {
      $("#filas", contenedor).innerHTML = `<tr><td class="vacio" colspan="99">${esc(e.message)}</td></tr>`;
    }
  }

  function pintarFilas() {
    const texto = estado.filtro.toLowerCase();
    const filas = estado.lista.filter(item => !texto ||
        modulo.columnas.some(c => String(c.valor(item, estado.ctx)).toLowerCase().includes(texto)));

    const cuerpo = $("#filas", contenedor);
    if (filas.length === 0) {
      cuerpo.innerHTML = `<tr><td class="vacio" colspan="99">${estado.lista.length === 0
          ? `Todavía no hay registros. ${soloLectura ? "" : `Use “Nuevo ${modulo.singular}” para agregar el primero.`}`
          : "Ningún registro coincide con la búsqueda."}</td></tr>`;
    } else {
      cuerpo.innerHTML = filas.map(item => `
        <tr>
          ${modulo.columnas.map(c => `<td class="${c.clase || ""}">${c.html ? c.valor(item, estado.ctx) : esc(c.valor(item, estado.ctx))}</td>`).join("")}
          <td><div class="acciones-fila">${botonesDeFila(item)}</div></td>
        </tr>`).join("");
    }
    $("#pie", contenedor).textContent = `Mostrando ${filas.length} de ${estado.lista.length} registros`;
  }

  function botonesDeFila(item) {
    const botones = [];
    (modulo.accionesExtra || []).forEach((accion, i) => {
      if (soloLectura && accion.editaDatos) return;
      if (accion.mostrar && !accion.mostrar(item)) return;
      botones.push(`<button class="accion ${accion.clase}" data-extra="${i}" data-id="${item.id}">${accion.texto}</button>`);
    });
    if (!soloLectura && (!modulo.editable || modulo.editable(item))) {
      botones.push(`<button class="accion editar" data-editar="${item.id}">Editar</button>`);
    }
    if (!soloLectura && modulo.eliminar && (!modulo.eliminar.mostrar || modulo.eliminar.mostrar(item))) {
      botones.push(`<button class="accion quitar" data-eliminar="${item.id}">${modulo.eliminar.texto}</button>`);
    }
    return botones.join("") || `<span class="ayuda">—</span>`;
  }

  /* ----- Clics en la tabla ----- */
  $("#filas", contenedor).addEventListener("click", async (evento) => {
    const boton = evento.target.closest("button");
    if (!boton) return;
    const buscarItem = (id) => estado.lista.find(x => String(x.id) === id);

    if (boton.dataset.editar) {
      abrirFormulario(buscarItem(boton.dataset.editar));
    } else if (boton.dataset.eliminar) {
      const item = buscarItem(boton.dataset.eliminar);
      if (!confirm(modulo.eliminar.confirmar(item))) return;
      await intentar(async () => {
        const r = await Api.delete(`${modulo.endpoint}/${item.id}`);
        avisar(r?.mensaje || "Listo.");
        recargar();
      });
    } else if (boton.dataset.extra !== undefined) {
      const accion = modulo.accionesExtra[boton.dataset.extra];
      await intentar(() => accion.ejecutar(buscarItem(boton.dataset.id), recargar));
    }
  });

  $("#buscar", contenedor).addEventListener("input", (e) => { estado.filtro = e.target.value; pintarFilas(); });

  /* ----- Formulario ----- */
  async function abrirFormulario(item) {
    editando = item || null;
    $("#titulo-form", contenedor).textContent = item
        ? `Editar ${modulo.singular}` : `Registrar ${modulo.singular}`;
    await construirCampos($("#campos", contenedor), modulo.campos, item);
    tarjetaForm.hidden = false;
    tarjetaForm.scrollIntoView({ behavior: "smooth", block: "start" });
    const primero = form.querySelector("input:not([disabled]), select:not([disabled])");
    if (primero) primero.focus();
  }

  function cerrarFormulario() {
    tarjetaForm.hidden = true;
    form.reset();
    editando = null;
  }

  const btnNuevo = $("#btn-nuevo", contenedor);
  if (btnNuevo) btnNuevo.addEventListener("click", () => intentar(() => abrirFormulario(null)));
  $("#btn-cancelar", contenedor).addEventListener("click", cerrarFormulario);

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    const dto = leerFormulario(form, modulo.campos, editando);
    if (!dto) return;
    await intentar(async () => {
      if (editando) {
        await Api.put(`${modulo.endpoint}/${editando.id}`, dto);
        avisar(`Se guardaron los cambios del ${modulo.singular}.`);
      } else {
        await Api.post(modulo.endpoint, dto);
        avisar(`Se registró el ${modulo.singular}.`);
      }
      cerrarFormulario();
      recargar();
    });
  });

  recargar();
}

/* ----- Construye los campos del formulario a partir de la descripción ----- */
async function construirCampos(contenedor, campos, item) {
  const visibles = campos.filter(c => !(c.soloAlEditar && !item));

  // Se cargan todas las listas desplegables al mismo tiempo
  const opciones = await Promise.all(visibles.map(c => {
    if (c.tipo !== "seleccion") return null;
    if (c.dependeDe) {
      const padre = campos.find(p => p.nombre === c.dependeDe);
      const valorPadre = item ? valorInicial(padre, item) : null;
      return c.cargarCon(valorPadre);
    }
    return c.cargar();
  }));

  contenedor.innerHTML = visibles.map((c, i) => {
    const valor = item ? valorInicial(c, item) : (c.inicial ? c.inicial() : "");
    const requerido = c.obligatorio || (c.obligatorioAlCrear && !item);
    const bloqueado = c.soloAlCrear && item ? "disabled" : "";
    const id = "campo-" + c.nombre;
    const etiqueta = `<label for="${id}" class="${requerido ? "obligatorio" : ""}">${c.etiqueta}</label>`;
    let control;

    switch (c.tipo) {
      case "seleccion":
        control = `<select id="${id}" name="${c.nombre}" ${requerido ? "required" : ""} ${bloqueado}>
          <option value="">Seleccione...</option>
          ${(opciones[i] || []).map(o => `<option value="${esc(o.valor)}" ${String(o.valor) === String(valor) ? "selected" : ""}>${esc(o.texto)}</option>`).join("")}
        </select>`;
        break;
      case "estado":
        return `<div class="campo"><span>Estado</span>
          <label class="check"><input type="checkbox" name="${c.nombre}" ${valor ? "checked" : ""}> ${c.etiqueta}</label></div>`;
      case "area":
        control = `<textarea id="${id}" name="${c.nombre}" ${requerido ? "required" : ""} placeholder="${c.placeholder || ""}">${esc(valor ?? "")}</textarea>`;
        break;
      default: {
        const tipos = { numero: "number", dinero: "number", fecha: "date", hora: "time", clave: "password" };
        const extra = [
          c.min !== undefined ? `min="${c.min}"` : "",
          c.max !== undefined ? `max="${c.max}"` : "",
          c.tipo === "dinero" ? `step="0.01" min="0"` : ""
        ].join(" ");
        const v = c.tipo === "hora" ? Formato.hora(valor).replace("—", "") : (valor ?? "");
        control = `<input id="${id}" name="${c.nombre}" type="${tipos[c.tipo] || "text"}" value="${esc(v)}"
          placeholder="${c.placeholder || ""}" ${requerido ? "required" : ""} ${bloqueado} ${extra}>`;
      }
    }
    return `<div class="campo ${c.ancho ? "ancho" : ""}">${etiqueta}${control}</div>`;
  }).join("");

  // Listas que dependen de otra (ej. las paradas dependen de la ruta elegida)
  visibles.filter(c => c.dependeDe).forEach(c => {
    const padre = contenedor.querySelector(`[name=${c.dependeDe}]`);
    const hijo = contenedor.querySelector(`[name=${c.nombre}]`);
    padre.addEventListener("change", async () => {
      hijo.innerHTML = `<option value="">Cargando...</option>`;
      const lista = await intentar(() => c.cargarCon(padre.value)) || [];
      hijo.innerHTML = `<option value="">${lista.length ? "Seleccione..." : "La ruta no tiene paradas"}</option>`
          + lista.map(o => `<option value="${o.valor}">${esc(o.texto)}</option>`).join("");
    });
  });
}

function valorInicial(campo, item) {
  return campo.desde ? campo.desde(item) : item[campo.nombre];
}

/* ----- Lee el formulario y arma el JSON (DTO) que espera la API ----- */
function leerFormulario(form, campos, editando) {
  if (!form.checkValidity()) {
    form.reportValidity();
    return null;
  }
  const dto = {};
  for (const c of campos) {
    const control = form.elements[c.nombre];
    if (!control || control.disabled) continue;

    if (c.tipo === "estado") { dto[c.nombre] = control.checked; continue; }

    const texto = control.value.trim();
    if (texto === "") { dto[c.nombre] = null; continue; }

    const esNumero = c.tipo === "numero" || c.tipo === "dinero" || c.nombre.endsWith("Id") || c.nombre === "mes";
    dto[c.nombre] = esNumero ? Number(texto) : texto;
  }
  return dto;
}

/* =============================================================
   5. PANTALLAS ESPECIALES
   ============================================================= */
const Paginas = {

  /* ---------- Panel de indicadores (inicio) ---------- */
  async panel(contenedor) {
    const mesActual = HOY.getMonth() + 1;
    contenedor.innerHTML = `
      <div class="encabezado">
        <div>
          <h1>Panel de control</h1>
          <p>Resumen del servicio de transporte al ${HOY.toLocaleDateString("es-GT", { day: "numeric", month: "long", year: "numeric" })}.</p>
        </div>
      </div>
      <div id="panel-cuerpo"><p class="ayuda">Cargando indicadores...</p></div>`;

    try {
      const [datos, disponibles, mantenimientos, pendientes] = await Promise.all([
        Api.get("/api/dashboard"),
        Api.get("/api/vehiculos/disponibles"),
        Api.get("/api/mantenimientos"),
        Api.get(`/api/pagos/pendientes?mes=${mesActual}&anio=${HOY.getFullYear()}`)
      ]);
      const enCurso = mantenimientos.filter(m => m.estado);
      const libres = datos.capacidadPorRuta.reduce((s, r) => s + r.disponibles, 0);

      $("#panel-cuerpo").innerHTML = `
        <div class="indicadores">
          <div class="indicador"><div class="valor">${datos.rutasActivas}</div><div class="etiqueta">Rutas activas</div></div>
          <div class="indicador amarillo"><div class="valor">${datos.estudiantesConTransporte}</div><div class="etiqueta">Estudiantes con transporte</div></div>
          <div class="indicador ${datos.pagosPendientesMesActual ? "rojo" : "verde"}"><div class="valor">${datos.pagosPendientesMesActual}</div><div class="etiqueta">Pagos pendientes de ${Formato.mes(mesActual)}</div></div>
          <div class="indicador verde"><div class="valor">${disponibles.length}</div><div class="etiqueta">Vehículos disponibles</div></div>
        </div>

        <section class="tarjeta">
          <div class="tarjeta-cabecera">
            <h2>Capacidad por ruta</h2>
            <div class="derecha leyenda">
              <span><i class="asiento ocupado"></i> Asiento ocupado</span>
              <span><i class="asiento"></i> Asiento libre</span>
              <span>${libres} asientos libres en total</span>
            </div>
          </div>
          <div class="lista-rutas">
            ${datos.capacidadPorRuta.length === 0
          ? `<p class="vacio">No hay rutas activas. Cree una ruta, agréguele paradas y actívela.</p>`
          : datos.capacidadPorRuta.map(Paginas.filaCapacidad).join("")}
          </div>
        </section>

        <div class="dos-columnas">
          <section class="tarjeta">
            <h2>Pagos pendientes de ${Formato.mes(mesActual)}</h2>
            ${pendientes.length === 0 ? `<p class="ayuda">Todos los estudiantes con transporte están al día.</p>` : `
              <div class="contenedor-tabla"><table>
                <thead><tr><th>Carné</th><th>Estudiante</th><th>Grado</th></tr></thead>
                <tbody>${pendientes.slice(0, 8).map(e => `<tr><td>${esc(e.carne)}</td><td>${esc(e.nombre)}</td><td>${esc(e.grado)}</td></tr>`).join("")}</tbody>
              </table></div>
              ${pendientes.length > 8 ? `<p class="pie-tabla">Y ${pendientes.length - 8} más. Vea la lista completa en Reportes.</p>` : ""}`}
          </section>
          <section class="tarjeta">
            <h2>Vehículos en mantenimiento</h2>
            ${enCurso.length === 0 ? `<p class="ayuda">Ningún vehículo está en mantenimiento.</p>` : `
              <div class="contenedor-tabla"><table>
                <thead><tr><th>Placa</th><th>Tipo</th><th>Desde</th></tr></thead>
                <tbody>${enCurso.map(m => `<tr><td>${esc(m.vehiculo.placa)}</td><td>${esc(m.tipo)}</td><td>${Formato.fecha(m.fechaInicio)}</td></tr>`).join("")}</tbody>
              </table></div>`}
          </section>
        </div>`;
    } catch (e) {
      $("#panel-cuerpo").innerHTML = `<section class="tarjeta"><p>${esc(e.message)}</p></section>`;
    }
  },

  // Cada cuadrito representa un asiento del bus
  filaCapacidad(r) {
    const maximo = 60;
    const mostrados = Math.min(r.capacidad, maximo);
    let asientos = "";
    for (let i = 0; i < mostrados; i++) {
      asientos += `<i class="asiento ${i < r.ocupados ? "ocupado" : ""}"></i>`;
    }
    const lleno = r.disponibles <= 0;
    return `
      <div class="ruta-capacidad">
        <div><h3>${esc(r.rutaNombre)}</h3><small>Capacidad del bus: ${r.capacidad}</small></div>
        <div class="asientos" role="img" aria-label="${r.ocupados} de ${r.capacidad} asientos ocupados">${asientos}${r.capacidad > maximo ? ` <small>+${r.capacidad - maximo}</small>` : ""}</div>
        <div class="cifras"><strong>${r.ocupados}/${r.capacidad}</strong><br>
          ${lleno ? `<span class="insignia no">Llena</span>` : `<small>${r.disponibles} libres</small>`}</div>
      </div>`;
  },

  /* ---------- Reportes y búsquedas ---------- */
  async reportes(contenedor) {
    const mesActual = HOY.getMonth() + 1;
    contenedor.innerHTML = `
      <div class="encabezado">
        <div>
          <h1>Reportes</h1>
          <p>Búsquedas y listados que pide el acta: estudiantes por carné, vehículos disponibles y pagos pendientes.</p>
        </div>
        <div class="acciones no-imprimir"><button class="btn btn-borde" onclick="window.print()">Imprimir</button></div>
      </div>

      <section class="tarjeta">
        <div class="tarjeta-cabecera">
          <h2>Buscar estudiante por carné</h2>
          <form class="derecha no-imprimir" id="form-carne">
            <input name="carne" placeholder="Ej. 2026-00" required aria-label="Carné">
            <button class="btn btn-primario">Buscar</button>
          </form>
        </div>
        <div id="res-carne"><p class="ayuda">Escriba el carné completo o una parte.</p></div>
      </section>

      <section class="tarjeta">
        <div class="tarjeta-cabecera">
          <h2>Pagos pendientes</h2>
          <form class="derecha no-imprimir" id="form-pendientes">
            <select name="mes" aria-label="Mes">${Opciones.meses().map(m => `<option value="${m.valor}" ${m.valor === mesActual ? "selected" : ""}>${m.texto}</option>`).join("")}</select>
            <input name="anio" type="number" value="${HOY.getFullYear()}" style="width:100px" aria-label="Año">
            <button class="btn btn-primario">Consultar</button>
          </form>
        </div>
        <div id="res-pendientes"></div>
      </section>

      <section class="tarjeta">
        <h2>Vehículos disponibles</h2>
        <div id="res-vehiculos"><p class="ayuda">Cargando...</p></div>
      </section>`;

    $("#form-carne").addEventListener("submit", async (e) => {
      e.preventDefault();
      const carne = e.target.carne.value.trim();
      await intentar(async () => {
        const estudiantes = await Api.get("/api/estudiantes/buscar?carne=" + encodeURIComponent(carne));
        const asignaciones = await Promise.all(estudiantes.map(est =>
            Api.get("/api/asignaciones?estudianteId=" + est.id)));
        $("#res-carne").innerHTML = estudiantes.length === 0
            ? `<p class="ayuda">No hay estudiantes con un carné que contenga “${esc(carne)}”.</p>`
            : Paginas.tabla(["Carné", "Nombre", "Grado", "Jornada", "Ruta y parada", "Estado"],
                estudiantes.map((est, i) => {
                  const activas = asignaciones[i].filter(a => a.estado);
                  return [esc(est.carne), esc(est.nombre), esc(est.grado), esc(est.jornada),
                    activas.length ? activas.map(a => `${esc(a.ruta.nombre)} / ${esc(a.parada.nombre)}`).join("<br>") : "Sin transporte",
                    insignia(est.estado)];
                }));
      });
    });

    const consultarPendientes = async (mes, anio) => {
      await intentar(async () => {
        const lista = await Api.get(`/api/pagos/pendientes?mes=${mes}&anio=${anio}`);
        $("#res-pendientes").innerHTML = lista.length === 0
            ? `<p class="ayuda">Nadie tiene pagos pendientes en ${Formato.mes(mes)} ${anio}.</p>`
            : Paginas.tabla(["Carné", "Estudiante", "Grado", "Jornada"],
                lista.map(e => [esc(e.carne), esc(e.nombre), esc(e.grado), esc(e.jornada)]))
            + `<p class="pie-tabla">${lista.length} estudiantes con transporte no han pagado ${Formato.mes(mes)} ${anio}.</p>`;
      });
    };
    $("#form-pendientes").addEventListener("submit", (e) => {
      e.preventDefault();
      consultarPendientes(e.target.mes.value, e.target.anio.value);
    });
    consultarPendientes(mesActual, HOY.getFullYear());

    intentar(async () => {
      const vehiculos = await Api.get("/api/vehiculos/disponibles");
      $("#res-vehiculos").innerHTML = vehiculos.length === 0
          ? `<p class="ayuda">No hay vehículos disponibles.</p>`
          : Paginas.tabla(["Placa", "Marca", "Modelo", "Año", "Capacidad"],
              vehiculos.map(v => [esc(v.placa), esc(v.marca), esc(v.modelo), v.anio, v.capacidad]));
    });
  },

  tabla(titulos, filas) {
    return `<div class="contenedor-tabla"><table>
      <thead><tr>${titulos.map(t => `<th>${t}</th>`).join("")}</tr></thead>
      <tbody>${filas.map(f => `<tr>${f.map(c => `<td>${c}</td>`).join("")}</tr>`).join("")}</tbody>
    </table></div>`;
  },

  /* ---------- Paradas de una ruta (se abre desde la tabla de rutas) ---------- */
  async paradasDeRuta(ruta) {
    const panel = $("#panel-extra");
    const soloLectura = permisos.soloLectura;

    const pintar = async () => {
      const [enRuta, todas] = await Promise.all([
        Api.get("/api/ruta-paradas?rutaId=" + ruta.id),
        Api.get("/api/paradas")
      ]);
      const usadas = new Set(enRuta.map(rp => rp.parada.id));
      const libres = todas.filter(p => p.estado && !usadas.has(p.id));
      const siguiente = enRuta.reduce((max, rp) => Math.max(max, rp.orden), 0) + 1;

      panel.innerHTML = `
        <section class="tarjeta" id="tarjeta-paradas">
          <div class="tarjeta-cabecera">
            <h2>Paradas de ${esc(ruta.nombre)}</h2>
            <div class="derecha"><button class="btn btn-borde btn-chico" id="cerrar-paradas">Cerrar</button></div>
          </div>
          ${enRuta.length === 0
          ? `<p class="ayuda">Esta ruta todavía no tiene paradas. Necesita al menos una para poder activarse.</p>`
          : `<div class="contenedor-tabla"><table>
                <thead><tr><th>Orden</th><th>Parada</th><th>Ubicación</th>${soloLectura ? "" : `<th class="centro">Acciones</th>`}</tr></thead>
                <tbody>${enRuta.map(rp => `
                  <tr>
                    <td><span class="numero-parada">${rp.orden}</span></td>
                    <td>${esc(rp.parada.nombre)}</td>
                    <td>${esc(rp.parada.ubicacion)}</td>
                    ${soloLectura ? "" : `<td><div class="acciones-fila parada-orden">
                      <input type="number" min="1" value="${rp.orden}" data-orden="${rp.id}" aria-label="Nuevo orden">
                      <button class="accion editar" data-guardar="${rp.id}">Cambiar orden</button>
                      <button class="accion quitar" data-quitar="${rp.id}">Quitar</button>
                    </div></td>`}
                  </tr>`).join("")}</tbody>
              </table></div>`}
          ${soloLectura ? "" : `
            <form class="rejilla-form" id="form-agregar-parada" style="margin-top:16px; align-items:end">
              <div class="campo"><label for="nueva-parada">Agregar parada</label>
                <select id="nueva-parada" name="paradaId" required>
                  <option value="">${libres.length ? "Seleccione..." : "No hay paradas libres"}</option>
                  ${libres.map(p => `<option value="${p.id}">${esc(p.nombre)} (${esc(p.ubicacion)})</option>`).join("")}
                </select></div>
              <div class="campo"><label for="nuevo-orden">Orden</label>
                <input id="nuevo-orden" name="orden" type="number" min="1" value="${siguiente}" required></div>
              <div class="campo"><button class="btn btn-guardar">Agregar a la ruta</button></div>
            </form>`}
        </section>`;

      $("#cerrar-paradas").onclick = () => { panel.innerHTML = ""; };
      const formAgregar = $("#form-agregar-parada");
      if (formAgregar) formAgregar.onsubmit = (e) => {
        e.preventDefault();
        intentar(async () => {
          await Api.post("/api/ruta-paradas", { rutaId: ruta.id, paradaId: Number(e.target.paradaId.value), orden: Number(e.target.orden.value) });
          avisar("Parada agregada a la ruta.");
          pintar();
        });
      };
      panel.querySelectorAll("[data-guardar]").forEach(b => b.onclick = () => intentar(async () => {
        const orden = Number(panel.querySelector(`[data-orden="${b.dataset.guardar}"]`).value);
        await Api.put("/api/ruta-paradas/" + b.dataset.guardar, { orden });
        avisar("Orden actualizado.");
        pintar();
      }));
      panel.querySelectorAll("[data-quitar]").forEach(b => b.onclick = () => {
        if (!confirm("¿Quitar esta parada de la ruta?")) return;
        intentar(async () => {
          await Api.delete("/api/ruta-paradas/" + b.dataset.quitar);
          avisar("Parada quitada de la ruta.");
          pintar();
        });
      });
    };

    await pintar();
    $("#tarjeta-paradas").scrollIntoView({ behavior: "smooth", block: "start" });
  }
};

iniciar();
