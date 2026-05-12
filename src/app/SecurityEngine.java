package app;

import malware.decoradores.*;
import sistema.Sistema;
import sistema.SistemaFactory;
import sistema.decoradores.*;
import ui.*;
import defensa.Antivirus;
import malware.Malware;
import malware.MalwareFactory;
import utilidades.Utils;

public class SecurityEngine {
	// ------------------------------- Atributos
	private static SecurityEngine instanciaUnica;
	private MenuConsola menu;

	private MalwareFactory malwareFactory;
	private SistemaFactory sistemaFactory;

	private Malware malwareAtacante;
	private Sistema sistemaDefensa;
	private Antivirus antivirus;

	// --------------------------- Constructores
	private SecurityEngine() {
		this.menu = new MenuConsola();
		this.malwareFactory = new MalwareFactory();
		this.sistemaFactory = new SistemaFactory();
	}

	// ------------------------ Métodos Públicos
	public static SecurityEngine getInstance() {
		if (instanciaUnica == null) {
			// Se crea solo la primera vez (Singleton)
			instanciaUnica = new SecurityEngine();
		}
		return instanciaUnica;
	}

	public void iniciarSimulacion() {

		AsciiArtManager.printhappyface();
		AsciiArtManager.printLogoPrincipal();

		// Elegir orden de creación
		int eleccion = menu.printMenuPrincipal();

		if (eleccion == 1) // Primero Malware
		{
			configurarMalware();
			menu.printTransicionASistema();
			configurarSistema();
		} else // Primero Sistema
		{
			configurarSistema();
			menu.printTransicionAMalware();
			configurarMalware();
		}

		// Aqui se accede a la arquiterura de sistema y se le pasa a malware
		this.malwareAtacante.setObjetivo(this.sistemaDefensa);
		printCreacion();

		// Se inicializa el antivirus
		Utils.esperar(1000);

		Antivirus antivirus = new Antivirus(sistemaDefensa, malwareAtacante);
		menu.printAntivirusArchivoDetectado();

		// Se llama al protocolo completo de defensa del antivirus
		antivirus.protocoloAntiIncidentes();
	}

	// ------------------------ Métodos Privados
	private void configurarMalware() {
		int pref = menu.printPreferenciaMalware();
		if (pref == 1) {
			int op = menu.printOpcionesPreconfiguradoMalware();
			this.malwareAtacante = malwareFactory.crearMalwarePreconfigurado(op);
		} else {

			this.malwareAtacante = malwareFactory.crearMalwareBase();

			String nombre = menu.printMalwareNombre();
			this.malwareAtacante = new NombreDecorator(this.malwareAtacante, nombre);

			int tipoOp = menu.printMalwareConfiguracion();
			switch (tipoOp) {
				case 1:
					this.malwareAtacante = new TroyanoDecorator(this.malwareAtacante);
					break;
				case 2:
					this.malwareAtacante = new RansomwareDecorator(this.malwareAtacante);
					break;
				case 3:
					this.malwareAtacante = new KeyloggerDecorator(this.malwareAtacante);
					break;
				default:
					this.malwareAtacante = new TroyanoDecorator(this.malwareAtacante);
					break;
			}

			int puntosSigilo = menu.printMalwareSigilo();
			this.malwareAtacante = new SigiloDecorator(this.malwareAtacante, puntosSigilo);

			this.malwareAtacante = new PropagacionDecorator(this.malwareAtacante, puntosSigilo);

			int opcionElegida = menu.printMalwareSistemaObjetivo();
			this.malwareAtacante = new SODecorator(this.malwareAtacante, opcionElegida);
		}
		menu.printMalwareListo();
	}

	private void configurarSistema() {
		int pref = menu.printPreferenciaSistema();
		if (pref == 1) {
			int op = menu.printOpcionesPreconfiguradoSistema();
			this.sistemaDefensa = sistemaFactory.crearSistemaPreconfigurado(op);
		} else {
			// Creación paso a paso con Decoradores de Sistema
			this.sistemaDefensa = sistemaFactory.crearSistemaBase();
			// 1. Envolvemos con el Nombre
			String nombre = menu.printSistemaNombre();
			this.sistemaDefensa = new NombreSistemaDecorator(this.sistemaDefensa, nombre);
			// 2. Definimos el sistema operativo
			int soOp = menu.printSistemaConfiguracion();
			switch (soOp) {
				case 1:
					this.sistemaDefensa = new WindowsDecorator(this.sistemaDefensa);
					break;
				case 2:
					this.sistemaDefensa = new MacDecorator(this.sistemaDefensa);
					break;
				case 3:
					this.sistemaDefensa = new LinuxDecorator(this.sistemaDefensa);
					break;
				default:
					this.sistemaDefensa = new WindowsDecorator(this.sistemaDefensa);
					break;
			}

			// 3. Envolvemos con la Arquitectura
			int arqOp = menu.printSistemaArquitectura();
			this.sistemaDefensa = new ArquitecturaDecorator(this.sistemaDefensa, arqOp);

			// 4. Envolvemos con la deteccion y automaticamente con la contencion
			int puntosDeteccionContencion = menu.printSistemaDeteccion();
			this.sistemaDefensa = new DeteccionDecorator(this.sistemaDefensa,
					puntosDeteccionContencion);
			this.sistemaDefensa = new ContencionDecorator(this.sistemaDefensa,
					puntosDeteccionContencion);
		}
		menu.printSistemaDesplegado();
	}

	private void printCreacion() {
		menu.printCreacion(sistemaDefensa, malwareAtacante);

		// Mostrar arte ASCII final
		printArteFinal();
	}

	private void printArteFinal() {
		malwareAtacante.printArte();
	}
}
