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

/**
 * @author afroufeq
 *
 */
public class TimerLatido extends Thread {
  private boolean arrancado = false;
  private int responseCode;
  private ArrayList<String> errores;

  public TimerLatido() {
    errores = new ArrayList<>();
  }

  @Override
  @SuppressWarnings( "SleepWhileInLoop" )
  public void run() {
    System.out.println( "TimerLatido -> enumerate[" + Thread.getAllStackTraces().size() + "]" );
    while( arrancado ) {
      // Usamos synchronized para evitar conflictos
      synchronized( this ) {
        // Cargamos el ficehro de configuración.
        // Lo cargamos cada una de las veces que se lanza el latido, porque eso nos permite actualizar el
        // fichero en caso de que se cambien las IPs de las instancias en donde corren los servicios
        if( cargaConfiguracion() ) {
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
        continue;
      }
    }
  }

  private void compruebaServicios() {
    // Comprobamos cada uno de los servicios de forma individual
    for( String servicio: Configuracion.getServicios() ) {
      System.out.println( "Comprobando -> " + servicio );
      if( !ping( servicio ) ) {
        System.out.println( "ERROR [" + responseCode + "]-> " + servicio );
        SesEmail.getInstance().sendErrores( errores );
      }
    }
  }

  private boolean cargaConfiguracion() {
    boolean ret = false;
    // Lo primero de todo es cargar la configuración, leyendo el fichero ini con los datos de las URL de los
    // servicios y los correos electrónicos de aviso de problemas
    if( Configuracion.readConfig() ) {
      System.out.println( "Procesado el fichero de configuracion" );
      System.out.println( "Servicios --- " );
      for( String servicio: Configuracion.getServicios() ) {
        System.out.println( "-> " + servicio );
      }
      System.out.println( "Emails --- " );
      for( String email: Configuracion.getEmails() ) {
        System.out.println( "-> " + email );
      }
      ret = true;
    }
    System.out.println( "Procesado el fichero de configuracion [" + ret + "]" );
    return ret;
  }

  /**
   * Pings a HTTP URL. This effectively sends a HEAD request and returns <code>true</code> if the response code is in
   * the 200-399 range.
   *
   * @param url     The HTTP URL to be pinged.
   * @param timeout The timeout in millis for both the connection timeout and the response read timeout. Note that
   *                the total timeout is effectively two times the given timeout.
   * @return <code>true</code> if the given HTTP URL has returned response code 200-399 on a HEAD request within the
   *         given timeout, otherwise <code>false</code>.
   */
  private boolean ping( String url ) {
    boolean ret = true;
    // Otherwise an exception may be thrown on invalid SSL certificates:
    url = url.replaceFirst( "^https","http" );
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
  }
}
