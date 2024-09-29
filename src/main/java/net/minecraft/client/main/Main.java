package net.minecraft.client.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.net.preferIPv4Stack", "true");
        OptionParser optionParser = new OptionParser();
        optionParser.allowsUnrecognizedOptions();
        optionParser.accepts("demo");
        optionParser.accepts("fullscreen");
        optionParser.accepts("checkGlErrors");
        OptionSpec<String> optionServer = optionParser.accepts("server").withRequiredArg();
        OptionSpec<Integer> optionPort = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565);
        OptionSpec<File> optionGameDir = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
        OptionSpec<File> optionAssetsDir = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
        OptionSpec<File> optionResourcePackDir = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
        OptionSpec<String> optionProxyHost = optionParser.accepts("proxyHost").withRequiredArg();
        OptionSpec<Integer> optionProxyPort = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
        OptionSpec<String> optionProxyUser = optionParser.accepts("proxyUser").withRequiredArg();
        OptionSpec<String> optionProxyPass = optionParser.accepts("proxyPass").withRequiredArg();
        OptionSpec<String> optionUsername = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L);
        OptionSpec<String> optionUuid = optionParser.accepts("uuid").withRequiredArg();
        OptionSpec<String> optionAccessToken = optionParser.accepts("accessToken").withRequiredArg().required();
        OptionSpec<String> optionVersion = optionParser.accepts("version").withRequiredArg().required();
        OptionSpec<Integer> optionWidth = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854);
        OptionSpec<Integer> optionHeight = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480);
        OptionSpec<String> optionUserProperties = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionProfileProperties = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}");
        OptionSpec<String> optionAssetIndex = optionParser.accepts("assetIndex").withRequiredArg();
        OptionSpec<String> optionUserType = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy");
        OptionSpec<String> nonOptions = optionParser.nonOptions();
        OptionSet optionSet = optionParser.parse(args);
        List<String> ignoredArguments = optionSet.valuesOf(nonOptions);

        if (!ignoredArguments.isEmpty())
        {
            System.out.println("Completely ignored arguments: " + ignoredArguments);
        }

        String proxyHost = optionSet.valueOf(optionProxyHost);
        Proxy proxy = Proxy.NO_PROXY;

        if (proxyHost != null) {
            try {
                proxy = new Proxy(Type.SOCKS, new InetSocketAddress(proxyHost, optionSet.valueOf(optionProxyPort)));
            } catch (Exception ignored) { }
        }

        final String proxyUsername = optionSet.valueOf(optionProxyUser);
        final String proxyPassword = optionSet.valueOf(optionProxyPass);

        if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(proxyUsername) && isNullOrEmpty(proxyPassword)) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyUsername, proxyPassword.toCharArray());
                }
            });
        }

        int width = optionSet.valueOf(optionWidth);
        int height = optionSet.valueOf(optionHeight);
        boolean fullscreen = optionSet.has("fullscreen");
        boolean checkGlErrors = optionSet.has("checkGlErrors");
        boolean demo = optionSet.has("demo");
        String version = optionSet.valueOf(optionVersion);
        Gson gson = (new GsonBuilder()).registerTypeAdapter(PropertyMap.class, new Serializer()).create();
        PropertyMap userProperties = gson.fromJson(optionSet.valueOf(optionUserProperties), PropertyMap.class);
        PropertyMap profileProperties = gson.fromJson(optionSet.valueOf(optionProfileProperties), PropertyMap.class);
        File gameDir = optionSet.valueOf(optionGameDir);
        File assetsDir = optionSet.has(optionAssetsDir) ? optionSet.valueOf(optionAssetsDir) : new File(gameDir, "assets/");
        File resourcePackDir = optionSet.has(optionResourcePackDir) ? optionSet.valueOf(optionResourcePackDir) : new File(gameDir, "resourcepacks/");
        String playerID = optionSet.has(optionUuid) ? optionUuid.value(optionSet) : optionUsername.value(optionSet);
        String assetIndex = optionSet.has(optionAssetIndex) ? optionAssetIndex.value(optionSet) : null;
        String server = optionSet.valueOf(optionServer);
        Integer port = optionSet.valueOf(optionPort);

        Session session = new Session(
                optionUsername.value(optionSet),
                playerID,
                optionAccessToken.value(optionSet),
                optionUserType.value(optionSet)
        );

        GameConfiguration gameConfig = new GameConfiguration(
                new GameConfiguration.UserInformation(session, userProperties, profileProperties, proxy),
                new GameConfiguration.DisplayInformation(width, height, fullscreen, checkGlErrors),
                new GameConfiguration.FolderInformation(gameDir, resourcePackDir, assetsDir, assetIndex),
                new GameConfiguration.GameInformation(demo, version),
                new GameConfiguration.ServerInformation(server, port)
        );

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
            @Override
            public void run() {
                Minecraft.stopIntegratedServer();
            }
        });

        Thread.currentThread().setName("Client thread");
        new Minecraft(gameConfig).run();
    }

    private static boolean isNullOrEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
