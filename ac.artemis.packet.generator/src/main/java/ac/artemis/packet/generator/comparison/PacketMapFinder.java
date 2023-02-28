package ac.artemis.packet.generator.comparison;

import ac.artemis.packet.generator.reflections.FieldAccessor;
import ac.artemis.packet.generator.reflections.ReflectUtil;
import ac.artemis.packet.generator.reflections.Reflection;
import ac.artemis.packet.generator.util.Pair;
import ac.artemis.packet.generator.util.ServerUtil;
import ac.artemis.packet.protocol.EnumProtocol;
import ac.artemis.packet.protocol.ProtocolDirection;
import ac.artemis.packet.protocol.ProtocolState;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.protocol.format.EnumProtocolFormat;
import ac.artemis.packet.wrapper.Packet;
import ac.artemis.packet.wrapper.PacketClient;
import ac.artemis.packet.wrapper.PacketInfo;
import ac.artemis.packet.wrapper.PacketMap;
import com.google.common.util.concurrent.AtomicDouble;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PacketMapFinder {
    private static final Map<String, Class<? extends Packet>> packets = new HashMap<>();

    public static EnumProtocolFormat findMap(ProtocolState state) {
        // Create the map
        PacketMap inbound = new PacketMap();
        PacketMap outbound = new PacketMap();

        // Get the map from the id to match the Spigot enum protocol
        Object enumProtocol = ReflectUtil.ENUM_PROTOCOLS[state.ordinal()];

        // For every direction, we'll seek to getting all the values from it's map
        for (int i = 0; i < ProtocolDirection.values().length; i++) {

            ProtocolDirection direction = ProtocolDirection.values()[i];

            // Get the map from the packet map
            Map map1 = ReflectUtil.PACKET_MAP_FIELD.get(enumProtocol);

            if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_15)) {
                try {
                    System.out.println(";) " + i);
                    Object interest = map1.get(ReflectUtil.DIRECTIONS[i]);

                    if (interest == null){
                        continue;
                    }

                    FieldAccessor<Map> fieldAccessor = Reflection.getField(interest.getClass(), Map.class, 0);
                    Map iterate = (Map) fieldAccessor.get(interest);

                    // For every value iterated, get the integer and the clazz and match the name
                    iterate.forEach((clazz, packetId) -> {
                        // Grab the packet ID
                        final int packet = (int) packetId;

                        // Grab the class
                        final Class claz = (Class) clazz;

                        // Convert name to string. This won't unfortunately work with obfuscated spigots. If
                        // You do obfuscate your spigots and rename the packets, it isn't my problem anymore.
                        // This API already supports for itself to be obfuscated. Don't be too needy >:(
                        String packetName = claz.getSimpleName();

                        final PacketInfo packetInfo = new PacketInfo(packet, getFuzzy(packetName), packetName);

                        if (direction == ProtocolDirection.IN) {
                            inbound.put(packet, packetInfo);
                        } else {
                            outbound.put(packet, packetInfo);
                        }
                    });

                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                Map interest = (Map) map1.get(ReflectUtil.DIRECTIONS[i]);

                // Map can be nullable. Just skip if it is
                if (interest == null){
                    continue;
                }

                // For every value iterated, get the integer and the clazz and match the name
                interest.forEach((packetId, clazz) -> {
                    // Grab the packet ID
                    int packet = (int) packetId;

                    // Grab the class
                    Class claz = (Class) clazz;

                    // Convert name to string. This won't unfortunately work with obfuscated spigots. If
                    // You do obfuscate your spigots and rename the packets, it isn't my problem anymore.
                    // This API already supports for itself to be obfuscated. Don't be too needy >:(
                    String packetName = claz.getSimpleName();

                    final PacketInfo packetInfo = new PacketInfo(packet, getFuzzy(packetName), packetName);

                    if (direction == ProtocolDirection.IN) {
                        inbound.put(packet, packetInfo);
                    } else {
                        outbound.put(packet, packetInfo);
                    }
                });
            }

        }

        // Return the map
        return new EnumProtocolFormat(state, inbound, outbound);
    }

    public static Class<? extends Packet> getFuzzy(final String clazz) {
        if (packets.get(clazz) != null) {
            return packets.get(clazz);
        }

        final AtomicReference<Class<? extends Packet>> closest = new AtomicReference<>();
        final AtomicDouble similarity = new AtomicDouble(-1);

        packets.forEach((name, clazzz) -> {
            name = name.replace("PacketPlay", "");
            final double sim = FuzzySearch.partialRatio(name, clazz.replace("PacketPlay", ""));

            if (sim > similarity.get() && sim > 90) {
                similarity.set(sim);
                closest.set(clazzz);
            }
        });

        System.out.println("Packet of type " + clazz + " has name " + closest.get() + " of similarity " + similarity.get());

        return closest.get();
    }

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname
     *            the package name to search
     * @return a list of classes that exist within that package
     * @throws ClassNotFoundException
     *             if something went wrong
     */
    private static List<Class> getClassesForPackage(String pckgname) throws ClassNotFoundException {
        // This will hold a list of directories matching the pckgname. There may be more than one if a package is split over multiple jars/paths
        ArrayList<File> directories = new ArrayList<File>();
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = pckgname.replace('.', '/');
            // Ask for all resources for the path
            Enumeration<URL> resources = cld.getResources(path);
            while (resources.hasMoreElements()) {
                directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
            }
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
        } catch (UnsupportedEncodingException encex) {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
        } catch (IOException ioex) {
            throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
        }

        ArrayList<Class> classes = new ArrayList<Class>();
        // For every directory identified capture all the .class files
        for (File directory : directories) {
            if (directory.exists()) {
                // Get the list of the files contained in the package
                String[] files = directory.list();
                for (String file : files) {
                    // we are only interested in .class files
                    if (file.endsWith(".class")) {
                        // removes the .class extension
                        try
                        {
                            classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
                        }
                        catch (NoClassDefFoundError e)
                        {
                            // do nothing. this class hasn't been found by the loader, and we don't care.
                        }
                    }
                }
            } else {
                throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
            }
        }
        return classes;
    }

    static {
        final Collection<URL> urls = new ArrayList<>(ClasspathHelper.forPackage("ac.artemis.packet.wrapper.client"));
        urls.addAll(ClasspathHelper.forPackage("ac.artemis.packet.wrapper.server"));
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(urls)
                .setScanners(new SubTypesScanner()))
                ;

        reflections.expandSuperTypes();

        final List<Class> classes = new ArrayList<>();

        try {
            classes.addAll(getClassesForPackage("ac.artemis.packet.wrapper.server"));
            classes.addAll(getClassesForPackage("ac.artemis.packet.wrapper.client"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Class<?> aClass : reflections.getSubTypesOf(Packet.class)) {
            if (!Packet.class.isAssignableFrom(aClass)) continue;
            final String name = aClass.getSimpleName().replace("Client", "In").replace("Server", "Out");
            packets.put(name, (Class) aClass);
            System.out.println("Loaded packet " + name);
        }

        for (Pair<String, Class<? extends Packet>> knownMap : HardcodeMap.knownMaps) {
            packets.put(knownMap.getX(), knownMap.getY());
        }
    }
}
