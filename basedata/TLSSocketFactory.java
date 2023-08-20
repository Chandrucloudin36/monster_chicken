package com.cloudin.monsterchicken.basedata;


import androidx.annotation.Nullable;

import com.cloudin.monsterchicken.TaskApplication;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


public class TLSSocketFactory extends SSLSocketFactory {

    private final SSLSocketFactory delegate;
    private TrustManager[] trustManagers;

    public TLSSocketFactory() throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException, CertificateException, IOException {
        generateTrustManagers();
        CertificateFactory mCertificateFactory = CertificateFactory.getInstance("X.509");
        InputStream mInputStream = TaskApplication.Companion.applicationContext().getResources().getAssets().open("superhr.crt");
        Certificate mCertificate;
        try {
            mCertificate = mCertificateFactory.generateCertificate(mInputStream);
        } finally {
            mInputStream.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore mKeyStore = KeyStore.getInstance(keyStoreType);
        mKeyStore.load(null, null);
        mKeyStore.setCertificateEntry("ca", mCertificate);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(mKeyStore);

        this.trustManagers = tmf.getTrustManagers();

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        delegate = context.getSocketFactory();
    }

    private void generateTrustManagers() throws KeyStoreException, NoSuchAlgorithmException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }

        this.trustManagers = trustManagers;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(delegate.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if (socket instanceof SSLSocket) {
            ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1.1", "TLSv1.2"});
        }
        return socket;
    }

    @Nullable
    public X509TrustManager getTrustManager() {
        return (X509TrustManager) trustManagers[0];
    }
}
