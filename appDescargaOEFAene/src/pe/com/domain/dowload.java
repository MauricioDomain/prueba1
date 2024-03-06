package pe.com.domain;

import java.io.*;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.print.attribute.standard.DocumentName;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pe.gob.sernanp.alfresco.bean.RptaBean;
import pe.gob.sernanp.alfresco.caller.CallServiceRest;
import pe.gob.sernanp.alfresco.util.Util;

public class dowload {

	private static String host;
	private static String port;
	private static String user;
	private static String password;
	private static String ruta_alfresco;
//	private static String ruta_carga;
	private static String ruta_descarga;
	private static String ruta_excel;
	private static String sheet;
	private static int val;


	// static HashMap<String, String> datos = new HashMap<String, String>();

	public static void main(String[] args) {

		Properties propiedad = new Properties();
		try {
			// args[0]
			propiedad.load(new FileReader(args[0]));

			host = propiedad.getProperty("HOST");
			port = propiedad.getProperty("PORT");
			user = propiedad.getProperty("USER");
			password = propiedad.getProperty("PASSWORD");
			ruta_excel = propiedad.getProperty("RUTA_EXCEL");
			ruta_descarga = propiedad.getProperty("RUTA_DESCARGA");
			sheet = propiedad.getProperty("SHEET");
			// lo convertirmos a entero
			val = Integer.parseInt(sheet);
		/*	// Divide la ruta en partes usando "/"
			String[] partes = ruta_excel.split("/");

			// Obtiene la última parte de la ruta
			String ultimoValor = partes[partes.length - 1];*/
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Ejecucion de la descarga");
		FileWriter fil = null;
		FileOutputStream fos = null;
		File folder = null, obj2 = null, obj3 = null;
		try {

			//System.out.println(ultimoValor);
			File excel = new File(ruta_excel);
			fil = new FileWriter(ruta_excel.replace(".xlsx", ".txt"));
			if (excel.exists()) {
				FileInputStream fis = new FileInputStream(excel);
				XSSFWorkbook wb = new XSSFWorkbook(fis);
				XSSFSheet sheet = wb.getSheetAt(val);
				Iterator<Row> rowIterator = sheet.iterator();
				rowIterator.next();

				while (rowIterator.hasNext()) {
					Row row = rowIterator.next();

					try {
						DataFormatter formatter = new DataFormatter();
						String order = formatter.formatCellValue(row.getCell(0));

						if (!order.isEmpty()) {

							
							String areaent=formatter.formatCellValue(row.getCell(0));
							
							String ndoc=formatter.formatCellValue(row.getCell(2));
							
							String uuid = formatter.formatCellValue(row.getCell(8));
							
							
							String area=areaent.substring(0,4);
                      
                            String ndocSinBarra = ndoc.replace('/', '-');
							RptaBean rptaBean = descargarArchivo(uuid);

							if (rptaBean != null) {
								System.out.println("CODIGO: " + rptaBean.getCode());
								System.out.println("MENSAJE: " + rptaBean.getMessage());
								if (rptaBean.getCode().equals("00000")) {

									String rutaCarpeta = ruta_descarga+"/"+area+"/"+ndocSinBarra;
									
									folder = new File(rutaCarpeta);
									if (!folder.exists()) {
										folder.mkdirs();
									}

									String rutaArchivo = rutaCarpeta + "/" + rptaBean.getFileName();
								
									
									try {

										fos = new FileOutputStream(rutaArchivo);
										fos.write(rptaBean.getContent());

										System.out.println("Documento Descargado -->" + "  Nombre Documento: "
												+ rptaBean.getFileName());

										fil.write(uuid + "  |  UUID:  " + rptaBean.getFileName() + "\n");
										System.out.println("\n");
										gc();
										fos.close();
									} catch (IOException e) {
										System.out.println("Error al guardar el archivo: " + e.getMessage());
									}
								} else {
									System.out.println("ERROR" + rptaBean.getException());
								}

							} else {

								System.out.println("No hay archivo");
							}
							fil.flush();
						}

					} catch (Exception e) {
						System.err.println("Error al obtener el documento");
						e.printStackTrace();
					}
				}
				wb.close();
				fil.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void gc() {
		Runtime garbage = Runtime.getRuntime();
		garbage.gc();
	}

	static RptaBean descargarArchivo(String uuid) {

		// CallServiceRest servicio=new CallServiceRest();
		// String rutaArchivoCarga=ruta_carga+"/"+documento;
		// String rutaAlfresco = ruta_alfresco;
		// String tipodoc = "esp:especificacion";
		// File file;
		try {

			// file = new File(rutaArchivoCarga);
			/// if (!uuid.equals("")) {

			return CallServiceRest.ServiceDownload(host, port, user, password, uuid);
			// } else {
			// System.out.println("No hay nada");
			// return null;

			// }
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
