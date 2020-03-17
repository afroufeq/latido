/*
 * Latido.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido;

import java.util.logging.Logger;


/**
 * Clase principal del monitor de servicios.
 * El monitor de servicios controla el acceso a los servicios que se configuran en el fichero de configuración
 * en el que se indica la URL de cada uno de los servicios a los que se quiere acceder.
 * Es posible indicar las llamadas a los servicios a través de la IP interna de Amazon, con lo que puede
 * distinguir perfectamente entre los distintos integrantes de un balanceador.
 * Cuando detecta que un servicio no responde, envía un email indicando esa circunstancia a los correos
 * que se configuren en el apartado correspondiente del fichero de configuración.
 *
 * @author afroufeq
 */
public class Latido {
  private final static Logger log = Logger.getLogger( Latido.class.getName() );
  private TimerLatido tl = null;

  public Latido() {
  }

  private void arrancaChequeo() {
    tl = new TimerLatido();
    tl.setArrancado( true );
    tl.run();
  }

  private void paraChequeo() {
    tl.setArrancado( false );
  }

  public static void main( String[] args ) {
    log.info( "LATIDO -> Arrancando.." );
    final Latido latido = new Latido();
    latido.arrancaChequeo();

    Runtime.getRuntime().addShutdownHook( new Thread() {
      @Override
      public void run() {
        log.info( "LATIDO -> Deteniendo.." );
        latido.paraChequeo();
      }
    } );

  }

}
