package test;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class Tu {
	public static void main(String[] args) throws Exception {
		String conf = new Tu().getClass().getResource("/").getPath()+"fdfs_client.conf";
		ClientGlobal.init(conf);
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		StorageServer storageServer = null;
		StorageClient1 storageClient = new StorageClient1(trackerServer, storageServer);
		String upload_file1 = storageClient.upload_file1("C:\\Users\\sxsqli\\Desktop\\txt\\abc\\1.jpg", "jpg", null);
		System.out.println(upload_file1);
	}
}
