package test;

import util.FastDFSClient;

public class Td {
	public static void main(String[] args) throws Exception {
		FastDFSClient fastDFSClient = new FastDFSClient("classpath:fdfs_client.conf");
		int deleteFile = fastDFSClient.deleteFile("group1/M00/00/00/wKgZhVtpvCaAZJAhAADvqD-yEJs894.jpg");
		System.out.println("完成"+deleteFile);
	}
}
