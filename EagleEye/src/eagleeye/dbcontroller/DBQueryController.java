package eagleeye.dbcontroller;

import eagleeye.entities.*;
import java.sql.*;
import java.util.ArrayList;

public class DBQueryController {

	protected int deviceID;
	protected DBQueries queryMaker;
		
	public DBQueryController() {
		
		this.deviceID = -1;
		queryMaker = new DBQueries();
	}
	
	public void setDeviceID(int deviceID){
		
		this.deviceID = deviceID;
	}
	
	public ArrayList<Device> getAllDevices() {
		
		ArrayList<Device> listOfDevices = new ArrayList<Device> (); 
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDevices());
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				Device device = new Device();
				device.modifyDeviceID(rs.getInt("DeviceID"));
				device.modifyDeviceName(rs.getString("DeviceName"));
				device.modifyDeviceOwner(rs.getString("DeviceOwner"));
				device.modifyContentSize(rs.getString("ContentSize"));
				device.modifyDateCreated(rs.getString("DateCreated"));
				device.modifyLastViewedOn(rs.getString("LastViewedOn"));
				
				listOfDevices.add(device);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfDevices;
	}
	
	
	//use for tree-view
	//Call this method if using tree
	public ArrayList<Directory> getAllDirectoriesAndFiles() {
		
		ArrayList<Directory> listOfDirectory = getAllDirectories();
		ArrayList<FileEntity> listOfFiles = getAllFiles();
		
		System.out.println("CaiJun: D_List Size =" + listOfDirectory.size());
		System.out.println("CaiJun: F_List Size =" + listOfFiles.size());
		
		ArrayList<Directory> organizedDirectoryList = this.organizeFilesAndDirectory(listOfDirectory, listOfFiles);
		
		return organizedDirectoryList;
		
	}
	
	public ArrayList<Directory> getAllDirectories(){
		
		ArrayList<Directory> listOfDirectory = new ArrayList<Directory>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllDirectories());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				Directory d = new Directory();
				d.modifyDirectoryID(rs.getInt("DirectoryID"));
				d.modifyDirectoryName(rs.getString("DirectoryName"));
				d.modifyDeviceID(rs.getInt("DeviceID"));
				d.modifyParentDirectory(rs.getInt("ParentDirectoryID"));
				d.modifyDateCreated(rs.getString("DateCreated"));
				d.modifyDateAccessed(rs.getString("DateAccessed"));
				d.modifyDateModified(rs.getString("DateModified"));
				d.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				d.modifyDateDeleted(rs.getString("DateDeleted"));
				
				listOfDirectory.add(d);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
				
		return listOfDirectory;
	}
	
	public ArrayList<FileEntity> getAllFiles() {
		
		ArrayList<FileEntity> listOfFiles = new ArrayList<FileEntity>();
		Connection conn = DBConnection.dbConnector();
		
		try {
			PreparedStatement stmt = conn.prepareStatement(queryMaker.getAllFiles());
			stmt.setInt(1, deviceID);
			
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()){
				
				FileEntity f = new FileEntity();
			
				f.modifyFileName(rs.getString("FileName"));
				f.modifyFileID(rs.getInt("FileID"));
				f.modifyDirectoryID(rs.getInt("DirectoryID"));
				f.modifyDirectoryName(rs.getString("DirectoryName"));
				f.modifyFileExt(rs.getString("FileExt"));
				f.modifyFileExtID(rs.getInt("FileExtID"));
				f.modifyDateCreated(rs.getString("DateCreated"));
				f.modifyDateAccessed(rs.getString("DateAccessed"));
				f.modifyDateModified(rs.getString("DateModified"));
				f.modifyIsModified(rs.getBoolean("IsModified"));
				f.modifyModifiedExt(rs.getString("ModifiedExt"));
				f.modifyIsRecovered(rs.getBoolean("IsRecovered"));
				f.modifyDateDeleted(rs.getString("DateDeleted"));
				f.modifyFilePath(rs.getString("FilePath"));
				f.modifyCategory(rs.getString("ExtTypeName"));
								
				listOfFiles.add(f);
			}
			
			conn.close();
			
		} catch (Exception e) {
			
			System.out.println("Query Failed");
			System.out.println(e.getMessage());
			try {
				conn.close();
			} catch (Exception e2) {
				
			}
		}
		
		return listOfFiles;
		
	}
	
	//Helper method
	private ArrayList<Directory> organizeFilesAndDirectory (ArrayList<Directory> listOfDirectory, ArrayList<FileEntity> listOfFiles){
		
		for(FileEntity f : listOfFiles) {
			
			int fileDirectory = f.getDirectoryID();
			
			for(Directory d : listOfDirectory) {
				
				int directoryID = d.getDirectoryID();
				
				if(fileDirectory == directoryID) {
					d.addNewFile(f);
					break;
				}
			}
		}
		
		return listOfDirectory;
		
	}
}
