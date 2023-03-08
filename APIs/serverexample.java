import java.security.KeyStore;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class ngserver {
        public static ngdatabase DB = new ngdatabase();

        private static void http() throws IOException {
                InetSocketAddress addr = new InetSocketAddress(80);
                HttpServer server = HttpServer.create(addr, 0);

                server.createContext("/nglogin", new nglogin());
                server.createContext("/ngrank", new ngrank());
                server.createContext("/ngactivate", new ngactivate());
                server.createContext("/ngquery", new ngquery());
                server.createContext("/ngrecyclehistory", new ngrecyclehistory());
                server.createContext("/ngpassword", new ngpassword());
                server.createContext("/ngspendhistory", new ngspendhistory());
                server.setExecutor(Executors.newCachedThreadPool());

                server.start();
        }

        //changing to https
        private static void https() throws IOException {
                try {
                        //getting the same stuff as http first
                        InetSocketAddress addr = new InetSocketAddress(443);
                        HttpsServer httpsServer = HttpsServer.create(addr, 0);

                        //some SSL stuff
                        SSLContext sslContext = SSLContext.getInstance("TLS");
                        char[] password = "ngpay".toCharArray();
                        //keystore is generated with
                        //private_key.key, site.crt, and site.ca-bundle
                        //using the following commands:
                        //openssl pkcs12 -export -out keystore.pkcs12 -inkey private_key.key -certfile site.ca-bundle -in site.crt
                        //    keytool -v -importkeystore -srckeystore keystore.pkcs12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype pkcs12
                        KeyStore ks = KeyStore.getInstance("PKCS12");
                        FileInputStream fis = new FileInputStream("./keystore.jks");
                        ks.load(fis, password);

                        //set up the key manager factory thingy
                        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                        kmf.init(ks, password);

                        //the trust manager now
                        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                        tmf.init(ks);

                        //setting up https context and specs
                        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                                @Override
                                public void configure(HttpsParameters params) {
                                        try {
                                                // initialise ssl context
                                                SSLContext c = getSSLContext();
                                                SSLEngine engine = c.createSSLEngine();
                                                params.setNeedClientAuth(false);
                                                params.setCipherSuites(engine.getEnabledCipherSuites());
                                                params.setProtocols(engine.getEnabledProtocols());

                                                // Set the SSL parameters
                                                SSLParameters sslParameters = c.getSupportedSSLParameters();
                                                params.setSSLParameters(sslParameters);

                                        } catch (Exception ex) {
                                                System.out.println("Failed to create HTTPS port");
                                                System.out.println(ex.getMessage());
                                        }
                                }
                        });
                        httpsServer.createContext("/nglogin", new nglogin());
                        httpsServer.createContext("/ngrank", new ngrank());
                        httpsServer.createContext("/ngactivate", new ngactivate());
                        httpsServer.createContext("/ngquery", new ngquery());
            httpsServer.createContext("/ngrecyclehistory", new ngrecyclghistory());
                httpsServer.createContext("/ngpassword", new ngpassword());
                        httpsServer.createContext("/ngspendhistory", new ngspendhistory());
                        httpsServer.setExecutor(Executors.newCachedThreadPool());
                    httpsServer.start();

                } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException ex) {
                        System.out.println("Failed to create HTTPS server on port 443");
                        System.out.println(ex.getMessage());
                }
        }

        public static void main(String[] args) throws IOException {
                https();
                return;
        }
}
