package cloud.waldiekiste.java.projekte.cloudnet.webinterface;

import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandSetupConfig;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandUpdateChannel;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.commands.CommandVersion;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.AuthenticationApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.CPermsApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.DashboardApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.MasterApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.MobApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.PlayerApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.ProxyApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.ServerApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.SignApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.UserApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.UtilsApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.http.v2.WrapperApi;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.listener.ScreenSessionListener;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.mob.MobDatabase;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.permission.ConfigPermissions;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.services.UpdateService;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.ConfigSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.DomainSslSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.setup.UpdateChannelSetup;
import cloud.waldiekiste.java.projekte.cloudnet.webinterface.sign.SignDatabase;
import de.dytanic.cloudnet.lib.NetworkUtils;
import de.dytanic.cloudnet.web.server.WebServer;
import de.dytanic.cloudnetcore.CloudNet;
import de.dytanic.cloudnetcore.api.CoreModule;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.ConfigurationException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

/**
 * This is the class, which is the base of the websocket-extension. At the startup, this class is
 * used by the Modulemanager.
 */
public final class ProjectMain extends CoreModule {

  /**
   * At this part, the basic Strings,Lists,Services, Setups and Maps  are listed.
   */
  private ConfigPermissions configPermission;
  private List<String> consoleLines;
  private Map<String, List<String>> screenInfos = new HashMap<>();
  private ConfigSetup configSetup;
  private UpdateChannelSetup updateChannelSetup;
  private UpdateService updateService;
  private SignDatabase signDatabase;
  private MobDatabase mobDatabase;
  private DomainSslSetup sslSetup;

  /**
   * In this method, the trackingservice, the updateservice and the classes are initialised.
   *
   * @see UpdateChannelSetup
   * @see UpdateService
   * @see ConfigSetup
   */
  @Override
  public void onLoad() {
    this.updateService = new UpdateService();
    this.consoleLines = new ArrayList<>();
    CloudNet.getLogger().getHandler().add(consoleLines::add);
    this.configSetup = new ConfigSetup();
    this.updateChannelSetup = new UpdateChannelSetup();
    this.sslSetup = new DomainSslSetup();

  }

  /**
   * Iniatilising API'S and checking version and counting the time between startup and end of the.
   * startup. Errorservice is still under development.
   *
   * @see ConfigPermissions
   * @see MasterApi
   * @see AuthenticationApi
   * @see ProxyApi
   * @see UserApi
   * @see DashboardApi
   * @see ServerApi
   * @see WrapperApi
   * @see UtilsApi
   */
  @Override
  public void onBootstrap() {
    boolean ssl = getCloud().getWebServer().isSsl();
    if (ssl) {
      System.out.println("You have enabled ssl option! Shutdown normal WebServer!");
      if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
          .contains("mdwi.domain")) {
        if(sslSetup == null){
          this.sslSetup = new DomainSslSetup();
        }
        this.sslSetup.start(CloudNet.getLogger().getReader());
      }
      getCloud().getWebServer().shutdown();
      Class<CloudNet> cloudNetClass = CloudNet.class;
      try {
        File certs = new File("certs");
        if (!certs.exists()) {
          if (certs.mkdirs()) {
            System.out.println("Certs folder successfully created!");
          }
        }
        Field webServer = cloudNetClass.getDeclaredField("webServer");
        webServer.setAccessible(true);
        WebServer server = new WebServer(false,
            getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
                .getString("mdwi.domain"),
            CloudNet.getInstance().getConfig().getWebServerConfig().getPort());

        Field sslContext = server.getClass().getDeclaredField("sslContext");
        sslContext.setAccessible(true);
        KeyStore keyStore = getKeyStore(new File(certs, "certFile.pem"),
            new File(certs, "keyFile.pem"), new File(certs, "caFile.pem"));
        KeyManagerFactory kmf = KeyManagerFactory
            .getInstance(Security.getProperty("ssl.KeyManagerFactory.algorithm"));
        kmf.init(keyStore,TEMPORARY_KEY_PASSWORD.toCharArray());
        SslContext context = SslContextBuilder.forServer(kmf).build();
        sslContext.set(server, context);
        webServer.set(getCloud(),server);
        getCloud().getWebServer().bind();
      } catch (NoSuchFieldException | IllegalAccessException | SSLException | CertificateException
          | InterruptedException | UnrecoverableKeyException | NoSuchAlgorithmException
          | KeyStoreException e) {
        e.printStackTrace();
      }
    }

    versionCheck();
    try {
      this.configPermission = new ConfigPermissions();
      this.signDatabase = new SignDatabase(
          this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
      this.mobDatabase = new MobDatabase(
          this.getCloud().getDatabaseManager().getDatabase("cloud_internal_cfg"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    getCloud().getCommandManager().registerCommand(new CommandSetupConfig(this));
    getCloud().getCommandManager().registerCommand(new CommandVersion(getVersion()));
    getCloud().getCommandManager().registerCommand(new CommandUpdateChannel(this));
    getCloud().getEventManager().registerListener(this, new ScreenSessionListener(this));
    new MasterApi(getCloud(), this);
    new AuthenticationApi();
    new ProxyApi(getCloud(), this);
    new UserApi(getCloud(), this);
    new DashboardApi(getCloud(), this);
    new ServerApi(getCloud(), this);
    new WrapperApi(getCloud());
    new UtilsApi(getCloud(), this);
    new PlayerApi(getCloud(), this);
    new SignApi(this);
    new MobApi(this);
    if (this.configPermission.isEnabled()) {
      new CPermsApi(this);
    }

  }

  /**
   * Clearing consoleLines & screenInfos for RAM "boost".
   */
  @Override
  public void onShutdown() {
    consoleLines = null;
    screenInfos = null;
  }

  /**
   * Checking Version + Checking functionality with the Cloudnet Version.
   */
  private void versionCheck() {
    if (this.configSetup == null) {
      this.updateChannelSetup = new UpdateChannelSetup();
    }
    if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
        .contains("mdwi.downgrade")) {
      if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
          .getBoolean("mdwi.downgrade")) {
        if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
            .contains("mdwi.updateChannel")) {
          this.updateChannelSetup.start(CloudNet.getLogger().getReader());
          //this.updateService.checkUpdate(this);
        } else {
          //this.updateService.checkUpdate(this);
        }
      }
    } else {
      if (!getCloud().getDbHandlers().getUpdateConfigurationDatabase().get()
          .contains("mdwi.updateChannel")) {
        this.updateChannelSetup.start(CloudNet.getLogger().getReader());
        //this.updateService.checkUpdate(this);
      } else {
        //this.updateService.checkUpdate(this);
      }
    }
    /*
     * Checking CloudNet Version and sending Error-Message if its lower than the version 2.1.8
     */
    if (Integer
        .parseInt(NetworkUtils.class.getPackage().getImplementationVersion().replace(".", ""))
        < 218) {
      System.err.println("This Module is not compatible with this CloudNet Version");
    }
  }

  /**
   * Here its getting the Updateservice and it is returning that service.
   *
   * @see UpdateService
   */
  public UpdateService getUpdateService() {
    return updateService;
  }

  /**
   * Here its getting the Configsetup and it is returning that setup.
   *
   * @see ConfigSetup
   */
  public ConfigSetup getConfigSetup() {
    return configSetup;
  }

  /**
   * Here its getting the ConfigPermission and its returning them.
   *
   * @see ConfigPermissions
   */
  public ConfigPermissions getConfigPermission() {
    return configPermission;
  }

  /**
   * Here its getting the ConsoleLines List and its returning the list.
   */
  public List<String> getConsoleLines() {
    return consoleLines;
  }

  /**
   * Here its getting a String, a List wich contains a String and the ScreenInfo Map, its returning
   * the map.
   */
  public Map<String, List<String>> getScreenInfos() {
    return screenInfos;
  }

  public SignDatabase getSignDatabase() {
    return signDatabase;
  }

  public MobDatabase getMobDatabase() {
    return mobDatabase;
  }

  private static final String TEMPORARY_KEY_PASSWORD = "changeit";

  private String fileToSring(File f) {
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(new FileInputStream(f)))) {
      return reader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private KeyStore getKeyStore(File certFile,File privateKeyFile, File caFile) {
    try {
      Certificate clientCertificate = loadCertificate(fileToSring(certFile));
      PrivateKey privateKey = loadPrivateKey(fileToSring(privateKeyFile));
      Certificate caCertificate = loadCertificate(fileToSring(caFile));

      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(null, null);
      keyStore.setCertificateEntry("ca-cert", caCertificate);
      keyStore.setCertificateEntry("client-cert", clientCertificate);
      keyStore.setKeyEntry("client-key", privateKey, TEMPORARY_KEY_PASSWORD.toCharArray(),
          new Certificate[]{clientCertificate});
      return keyStore;
    } catch (GeneralSecurityException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private Certificate loadCertificate(String certificatePem) throws IOException,
      GeneralSecurityException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
    final byte[] content = readPemContent(certificatePem);
    return certificateFactory.generateCertificate(new ByteArrayInputStream(content));
  }

  private PrivateKey loadPrivateKey(String privateKeyPem) throws IOException,
      GeneralSecurityException {
    return pemLoadPrivateKeyPkcs1OrPkcs8Encoded(privateKeyPem);
  }

  private byte[] readPemContent(String pem) throws IOException {
    final byte[] content;
    try (PemReader pemReader = new PemReader(new StringReader(pem))) {
      PemObject pemObject = pemReader.readPemObject();
      content = pemObject.getContent();
    }
    return content;
  }

  private static PrivateKey pemLoadPrivateKeyPkcs1OrPkcs8Encoded(
      String privateKeyPem) throws GeneralSecurityException, IOException {
    // PKCS#8 format
    final String pemPrivateStart = "-----BEGIN PRIVATE KEY-----";
    final String pemPrivateEnd = "-----END PRIVATE KEY-----";

    // PKCS#1 format
    final String pemRsaPrivateStart = "-----BEGIN RSA PRIVATE KEY-----";
    final String pemRsaPrivateEnd = "-----END RSA PRIVATE KEY-----";

    if (privateKeyPem.contains(pemPrivateStart)) { // PKCS#8 format
      privateKeyPem = privateKeyPem.replace(pemPrivateStart, "")
          .replace(pemPrivateEnd, "");
      privateKeyPem = privateKeyPem.replaceAll("\\s", "");

      byte[] pkcs8EncodedKey = Base64.getDecoder().decode(privateKeyPem);

      KeyFactory factory = KeyFactory.getInstance("RSA");
      return factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedKey));

    } else if (privateKeyPem.contains(pemRsaPrivateStart)) {  // PKCS#1 format

      privateKeyPem = privateKeyPem.replace(pemRsaPrivateStart, "")
          .replace(pemRsaPrivateEnd, "");
      privateKeyPem = privateKeyPem.replaceAll("\\s", "");

      DerInputStream derReader = new DerInputStream(Base64.getDecoder().decode(privateKeyPem));

      DerValue[] seq = derReader.getSequence(0);

      if (seq.length < 9) {
        throw new GeneralSecurityException("Could not parse a PKCS1 private key.");
      }

      // skip version seq[0];
      BigInteger modulus = seq[1].getBigInteger();
      BigInteger publicExp = seq[2].getBigInteger();
      BigInteger privateExp = seq[3].getBigInteger();
      BigInteger prime1 = seq[4].getBigInteger();
      BigInteger prime2 = seq[5].getBigInteger();
      BigInteger exp1 = seq[6].getBigInteger();
      BigInteger exp2 = seq[7].getBigInteger();
      BigInteger crtCoef = seq[8].getBigInteger();

      RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp,
          privateExp, prime1, prime2, exp1, exp2, crtCoef);

      KeyFactory factory = KeyFactory.getInstance("RSA");

      return factory.generatePrivate(keySpec);
    }

    throw new GeneralSecurityException("Not supported format of a private key");
  }
}
