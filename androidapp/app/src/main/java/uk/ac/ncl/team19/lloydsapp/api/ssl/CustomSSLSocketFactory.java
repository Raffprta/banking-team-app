package uk.ac.ncl.team19.lloydsapp.api.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Class for trusting SSL certificates from additional keystores, allowing the use of
 * Newcastle University's self-signed certificate for https://homepages.cs.ncl.ac.uk/.
 * @author Dale Whinham
 *
 * Based on answer from StackOverflow: http://stackoverflow.com/questions/2642777/trusting-all-certificates-using-httpclient-over-https
 */
public class CustomSSLSocketFactory extends SSLSocketFactory {
    protected final SSLContext sslContext;

    public CustomSSLSocketFactory(KeyStore keyStore) throws NoSuchAlgorithmException, KeyManagementException {
        super();
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new AdditionalKeyStoresTrustManager(keyStore)}, null);
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return sslContext.getServerSocketFactory().getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return sslContext.getServerSocketFactory().getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return sslContext.getSocketFactory().createSocket(host, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return sslContext.getSocketFactory().createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return sslContext.getSocketFactory().createSocket(address, port, localAddress, localPort);
    }

    // Based on http://download.oracle.com/javase/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#X509TrustManager
    private class AdditionalKeyStoresTrustManager implements X509TrustManager {

        final ArrayList<X509TrustManager> x509TrustManagers = new ArrayList<>();

        AdditionalKeyStoresTrustManager(KeyStore... additionalKeyStores) {
            ArrayList<TrustManagerFactory> factories = new ArrayList<>();

            try {
                // The system default TrustManager with default keystore
                TrustManagerFactory defaultFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                defaultFactory.init((KeyStore) null);
                factories.add(defaultFactory);

                // Add additional trust managers for each additional keystore
                for (KeyStore keyStore: additionalKeyStores) {
                    TrustManagerFactory additionalCertsFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    additionalCertsFactory.init(keyStore);
                    factories.add(additionalCertsFactory);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Add any X509TrustManagers to our list
            for (TrustManagerFactory tmf: factories) {
                for (TrustManager tm : tmf.getTrustManagers()) {
                    if (tm instanceof X509TrustManager) {
                        x509TrustManagers.add((X509TrustManager) tm);
                    }
                }
            }

            if (x509TrustManagers.isEmpty()) {
                throw new RuntimeException("Couldn't find any X509TrustManagers");
            }
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // Just use default Trust Manager to verify clients
            x509TrustManagers.get(0).checkClientTrusted(chain, authType);
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            CertificateException exception = null;

            // Iterate through Trust Managers until we find one that can verify the server
            for (X509TrustManager tm: x509TrustManagers) {
                try {
                    tm.checkServerTrusted(chain, authType);
                    return;
                } catch (CertificateException e) {
                    exception = e;
                }
            }

            // Throw the last exception if the server couldn't be verified
            throw exception;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // Return issuers from all trust managers
            ArrayList<X509Certificate> list = new ArrayList<>();
            for (X509TrustManager tm: x509TrustManagers) {
                list.addAll(Arrays.asList(tm.getAcceptedIssuers()));
            }
            return (X509Certificate[]) list.toArray();
        }
    }
}