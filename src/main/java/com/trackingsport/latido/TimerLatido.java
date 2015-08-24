/*
 * TimerLatido.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido;

import com.trackingsport.latido.utils.Configuracion;
import com.trackingsport.latido.utils.Constantes;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * @author afroufeq
 *
 */
public class TimerLatido extends Thread {
  private final Logger log = Logger.getLogger( this.getClass() );
  private boolean arrancado = false;
  private int responseCode;
  private ArrayList<String> errores;

  public TimerLatido() {
    errores = new ArrayList<>();
  }

  @Override
  @SuppressWarnings( "SleepWhileInLoop" )
  public void run() {
    log.debug( "TimerLatido -> enumerate[" + Thread.getAllStackTraces().size() + "]" );
    while( arrancado ) {
      // Usamos synchronized para evitar conflictos
      synchronized( this ) {
        // Cargamos el fichero de configuración.
        // Lo cargamos cada una de las veces que se lanza el latido, porque eso nos permite actualizar el
        // fichero en caso de que se cambien las IPs de las instancias en donde corren los servicios
        if( cargaConfiguracion() ) {
          log.info( "TimeLatido | Ejecucion "+Configuracion.getFecha() );
          // borramos todos los mensajes de error que hubiesen podido generarse en la anterior ejecución
          errores.clear();
          // Comprobamos los servicios
          compruebaServicios();
        }
        else {
          SesEmail.getInstance().sendFalloConfiguracion();
        }
      }
      // Congelamos el timer el tiempo que se fije en el intervalo 
      try {
        Thread.sleep( Constantes.INTERVALO_CHECK );
      }catch( InterruptedException ie ) {
        System.out.println( "EX -> " + ie.getMessage() );
        continue;
      }
    }
  }

  private void compruebaServicios() {
    // Comprobamos cada uno de los servicios de forma individual
    for( String servicio: Configuracion.getServicios() ) {
      log.debug( "TimerLatido.compruebaServicios | Comprobando -> " + servicio );
      if( !ping( servicio ) ) {
        log.info( "TimerLatido.compruebaServicios | Error[" + responseCode + "]-> " + servicio );
      }
    }
    if( !errores.isEmpty() ) {
      SesEmail.getInstance().sendErrores( errores );
    }
  }

  private boolean cargaConfiguracion() {
    boolean ret = false;
    // Lo primero de todo es cargar la configuración, leyendo el fichero ini con los datos de las URL de los
    // servicios y los correos electrónicos de aviso de problemas
    if( Configuracion.readConfig() ) {
      log.debug( "Procesado el fichero de configuracion" );
      log.debug( "Servicios --- " );
      for( String servicio: Configuracion.getServicios() ) {
        log.debug( "-> " + servicio );
      }
      log.debug( "Emails --- " );
      for( String email: Configuracion.getEmails() ) {
        log.debug( "-> " + email );
      }
      ret = true;
    }
    log.info( "TimerLatido.cargaConfiguracion | [" + ret + "]" );
    return ret;
  }

  private boolean ping( String url ) {
    boolean ret = true;
    // Se puede lanzar una excepción si los certificados SSL no son válidos
//    url = url.replaceFirst( "^https","http" );
    try {
      HttpURLConnection connection = (HttpURLConnection)new URL( url ).openConnection();
      // Fijamos el tiempo en que esperamos la respuesta antes de considerar un fallo
      connection.setConnectTimeout( Constantes.TIMEOUT );
      connection.setReadTimeout( Constantes.TIMEOUT );
      connection.setRequestMethod( "HEAD" );
      responseCode = connection.getResponseCode();
      if( 200 <= responseCode && responseCode <= 399 ) {
        System.out.println( "OK [" + url + "] " + Configuracion.codeStr( responseCode ) );
      }
      else {
        ret = false;
        errores.add( Configuracion.codeStr( responseCode ) + ", " + url );
      }
    }catch( IOException e ) {
      ret = false;
      errores.add( "EX-" + e.getMessage() + " [" + url + "]" );
    }
    return ret;
  }

  public void setArrancado( boolean arrancado ) {
    this.arrancado = arrancado;
    if( arrancado == false ) {
      this.interrupt();
    }
  }
}
