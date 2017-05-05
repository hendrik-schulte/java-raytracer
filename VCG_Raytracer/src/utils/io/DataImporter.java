package utils.io;

import com.interactivemesh.jfx.importer.obj.ObjModelImporter;
import javafx.scene.shape.MeshView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataImporter {

	public DataImporter() {}

	public static MeshView[] loadOBJ(String path) {

		Path p = Paths.get(path);
		Log.print(DataImporter.class, "loading " + p.toAbsolutePath());
		File file = new File(p.toAbsolutePath().toString());
//		File file = loadFile(p.toAbsolutePath().toString());
//		File file = loadFile(path);
//		File file = Class.class.getResource(path).;

//		Log.print(DataImporter.class, "file: " + file.getName() + " exists: " + file.exists());

		ObjModelImporter importer = new ObjModelImporter();
		importer.read(file);
		MeshView[] meshes = importer.getImport();

		Log.print(DataImporter.class, "... done!");

		return meshes;
	}

	public static void loadTexture(String path) {
		File file = loadFile(path);

		Log.print(DataImporter.class, "... done!");
	}

	private static File loadFile(String path){
		Log.print(DataImporter.class, "Start importing " + path + " ...");

		return new File(path);
	}
}
