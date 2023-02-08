package ac.artemis.core.v4.utils.file;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Ghast
 * @since 28-Apr-20
 */
public class JarFileLoader extends URLClassLoader {
    public JarFileLoader(URL[] urls) {
        super(urls);
    }

    public void addURL(String url) {
        String urlPath = "jar:file://" + url + "!/";
        try {
            super.addURL(new URL(urlPath));
        } catch (MalformedURLException e) {
            throw new DepedencyLoadException("File at URL " + url + " has malformed URL", e);
        }
    }

    public void addJarToClasspath(File jar) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
        // Get the ClassLoader class
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Class<?> clazz = cl.getClass();

        // Get the protected addURL method from the parent URLClassLoader class
        Method method = clazz.getSuperclass().getDeclaredMethod("addURL", new Class[]{URL.class});

        // Run projected addURL method to add JAR to classpath
        method.setAccessible(true);
        method.invoke(cl, new Object[]{jar.toURI().toURL()});
    }

    /*
    public void addJarToClasspath(File jar) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException {
        // Get the ClassLoader class
        ClassLoader cl = Artemis.INSTANCE.getPlugin().getClass().getClassLoader();
        Class<? extends URLClassLoader> clazz = URLClassLoader.class;

        // Get the protected addURL method from the parent URLClassLoader class
        Method method = clazz.getSuperclass().getDeclaredMethod("addURL", URL.class);

        // Run projected addURL method to add JAR to classpath
        //addURL(jar.toURI().toURL());


        method.setAccessible(true);
        method.invoke(cl, jar.toURI().toURL());
    }
     */
}
