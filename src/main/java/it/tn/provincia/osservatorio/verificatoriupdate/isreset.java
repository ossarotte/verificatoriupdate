/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.tn.provincia.osservatorio.verificatoriupdate;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author pr41103
 */
class isreset extends InputStream {

    private final InputStream decorated;

    public isreset(InputStream anInputStream) {
        /*if (!anInputStream.markSupported()) {
            throw new IllegalArgumentException("marking not supported");
        }*/
        
        anInputStream.mark( 1 << 24); // magic constant: BEWARE
        decorated = anInputStream;
    }

    @Override
    public void close() throws IOException {
        decorated.reset();
    }

    @Override
    public int read() throws IOException {
        return decorated.read();
    }
}
