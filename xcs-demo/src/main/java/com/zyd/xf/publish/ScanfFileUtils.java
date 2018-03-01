//package com.zyd.xf.publish;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//
//import org.apache.poi.EncryptedDocumentException;
//import org.apache.poi.hssf.usermodel.HSSFCell;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.ss.usermodel.WorkbookFactory;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//
//import com.xcs.utils.MapperUtil;
//
//public class ScanfFileUtils {
//
//	public static final int SCANF_EXCEL_CELL = 3;
//
//	public static final int SCANF_EXCEL_ROW = 2;
//	
//	public static final int SCANF_EXCEL_ROW_END = 22;
//
//	public static final int SCANF_EXCEL_SHEET = 1;
//
//	public static final int SCANF_EXCEL_END = 0;
//
//	public static void main(String[] args) {
//		try {
//			InputStream in = new FileInputStream(new File("D:\\upload\\2017040709.xls"));
//			scanfExcel(in, new ScanfOpt() { 
//				@Override
//				public boolean excute(Object obj,int scanfNum, int scanfType) {
//					if (scanfType == ScanfFileUtils.SCANF_EXCEL_CELL && obj != null) {
//						//System.out.print("第"+scanfNum + "列:" + ScanfFileUtils.getExcelCellValue((Cell) obj) + "  ");
//					} else if (scanfType == ScanfFileUtils.SCANF_EXCEL_ROW && obj != null) {
//						System.out.println();
//						System.out.print("第"+scanfNum + "行开始:");
//					} else if (scanfType == ScanfFileUtils.SCANF_EXCEL_ROW_END && obj != null) {
//						System.out.print("第"+scanfNum + "行结束:");
//						System.out.println();
//					} else if (scanfType == SCANF_EXCEL_SHEET && obj != null) {
//						//System.out.println();
//						//System.out.print("第"+scanfNum + "个工作区:");
//					} else if (scanfType == SCANF_EXCEL_END) {
//						//System.out.println();
//						//System.out.println("扫描所有sheet结束"+scanfNum);
//					}
//					return true;
//				}
//			});
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * 
//	 * 扫描Excel文档
//	 * 
//	 * @param in
//	 * @param scanfOpt
//	 * @throws EncryptedDocumentException
//	 * @throws InvalidFormatException
//	 * @throws IOException
//	 */
//	public static void scanfExcel(InputStream in, ScanfOpt scanfOpt) {
//		Workbook wb = null;
//		try {
//			wb = WorkbookFactory.create(in);
//		} catch (EncryptedDocumentException e) {
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		if (wb != null) {
//			for (int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
//				Sheet sheet = wb.getSheetAt(sheetNum);
//				if (!scanfOpt.excute(sheet, sheetNum, SCANF_EXCEL_SHEET)) {
//					break;
//				}
//				for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
//					Row row = sheet.getRow(rowNum);
//					if (!scanfOpt.excute(row, rowNum, SCANF_EXCEL_ROW)) {
//						break;
//					}
//					for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
//						Cell cell = row.getCell(cellNum);
//						if (!scanfOpt.excute(cell, cellNum, SCANF_EXCEL_CELL)) {
//							break;
//						}
//					}
//					if (!scanfOpt.excute(row, rowNum, SCANF_EXCEL_ROW_END)) {
//						break;
//					}
//				}
//			}
//			scanfOpt.excute(null,-1, SCANF_EXCEL_END);
//		} else {
//			throw new ScanfFormatNotSupoortException("can not support this file, please check you file");
//		}
//	}
//
//	public static String getExcelCellValue(Cell cell) {
//		if (cell instanceof XSSFCell) {
//			return getXSSFCellValue((XSSFCell) cell);
//		} else if (cell instanceof HSSFCell) {
//			return getHSSFCellValue((HSSFCell) cell);
//		}
//		throw new ScanfFormatNotSupoortException("cell can not cast to XSSFCell or HSSFCell");
//	}
//
//	private static String getXSSFCellValue(XSSFCell xssfCell) {
//		if (xssfCell == null) {
//			return null;
//		}
//		if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
//			return MapperUtil.sqlValidate(String.valueOf(xssfCell.getBooleanCellValue()));
//		} else if (xssfCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
//			return MapperUtil.sqlValidate(new Long((long) xssfCell.getNumericCellValue()).toString());
//		} else {
//			return MapperUtil.sqlValidate(String.valueOf(xssfCell.getStringCellValue()));
//		}
//	}
//
//	private static String getHSSFCellValue(HSSFCell hssfCell) {
//		if (hssfCell == null) {
//			return null;
//		}
//		if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
//			return MapperUtil.sqlValidate(String.valueOf(hssfCell.getBooleanCellValue()));
//		} else if (hssfCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//			return MapperUtil.sqlValidate(new Long((long) hssfCell.getNumericCellValue()).toString());
//		} else {
//			return MapperUtil.sqlValidate(String.valueOf(hssfCell.getStringCellValue()));
//		}
//	}
//
//	static class ScanfFormatNotSupoortException extends RuntimeException {
//
//		private static final long serialVersionUID = -5246513675271823919L;
//
//		public ScanfFormatNotSupoortException() {
//			super();
//		}
//
//		public ScanfFormatNotSupoortException(String msg) {
//			super(msg);
//		}
//
//	}
//
//	/**
//	 * 操作接口
//	 * @author Administrator
//	 *
//	 */
//	public interface ScanfOpt {
//		public boolean excute(Object obj, int scanfNum, int scanfType);
//	}
//	
//}
