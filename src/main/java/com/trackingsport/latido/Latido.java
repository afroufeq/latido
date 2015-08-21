/*
 * Latido.java
 * Proyecto:  Monitor de servicios
 * Copyright: 2015, TrackingSport
 */
package com.trackingsport.latido;

import com.trackingsport.latido.utils.Configuracion;
import com.trackingsport.latido.utils.Constantes;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
  private int responseCode;

  public Latido() {
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
    else {
      System.out.println( "FATAL: Error en la configuracion" );
      System.exit( 1 );
    }
    return ret;
  }

  private void compruebaServicios() {
    for( String servicio: Configuracion.getServicios() ) {
      System.out.println( "Comprobando -> " + servicio );
      if( !ping( servicio,Constantes.TIMEOUT ) ) {
        System.out.println( "ERROR [" + responseCode + "]-> " + servicio );
      }
    }

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
  private boolean ping( String url,int timeout ) {
    // Otherwise an exception may be thrown on invalid SSL certificates:
    url = url.replaceFirst( "^https","http" );

    try {
      HttpURLConnection connection = (HttpURLConnection)new URL( url ).openConnection();
//      connection.setConnectTimeout( timeout );
//      connection.setReadTimeout( timeout );
      connection.setRequestMethod( "HEAD" );
      responseCode = connection.getResponseCode();
      return ( 200 <= responseCode && responseCode <= 399 );
    }catch( IOException exception ) {
      return false;
    }
  }

  public static void main( String[] args ) {
    Latido latido = new Latido();
    if( latido.cargaConfiguracion() ) {
      System.out.println( "Configuracion cargada!" );
      latido.compruebaServicios();
    }
  }

}
