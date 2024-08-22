package cn.tamilin.api.client;


import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.junit.Test;
import org.slf4j.Logger;

public class TestPOI {

	private static final Logger logger = getLogger(TestPOI.class);

	private static class EventExample implements HSSFListener {

		private SSTRecord sstrec;

		/**
		 * This method listens for incoming records and handles them as required.
		 * @param record    The record that was found while reading.
		 */
		public void processRecord(Record record) {
			switch (record.getSid()) {
			// the BOFRecord can represent either the beginning of a sheet or the workbook
			case BOFRecord.sid:
				BOFRecord bof = (BOFRecord) record;
				if (bof.getType() == BOFRecord.TYPE_WORKBOOK) {
					logger.info("Encountered workbook");
					// assigned to the class level member
				} else if (bof.getType() == BOFRecord.TYPE_WORKSHEET) {
					logger.info("Encountered sheet reference");
				}
				break;
			case BoundSheetRecord.sid:
				BoundSheetRecord bsr = (BoundSheetRecord) record;
				logger.info("New sheet named: " + bsr.getSheetname());
				break;
			case RowRecord.sid:
				RowRecord rowrec = (RowRecord) record;
				logger.info("Row found, first column at " + rowrec.getFirstCol() + " last column at " + rowrec.getLastCol());
				break;
			case NumberRecord.sid:
				NumberRecord numrec = (NumberRecord) record;
				logger.info("Cell found with value " + numrec.getValue() + " at row " + numrec.getRow() + " and column " + numrec.getColumn());
				break;
			// SSTRecords store a array of unique strings used in Excel.
			case SSTRecord.sid:
				sstrec = (SSTRecord) record;
				for (int k = 0; k < sstrec.getNumUniqueStrings(); k++) {
					logger.info("String table value " + k + " = " + sstrec.getString(k));
				}
				break;
			case LabelSSTRecord.sid:
				LabelSSTRecord lrec = (LabelSSTRecord) record;
				logger.info("String cell found with value " + sstrec.getString(lrec.getSSTIndex()));
				break;
			}
		}
	}

	@Test
	public void test() throws EncryptedDocumentException, InvalidFormatException, IOException {
		try (InputStream fin = new FileInputStream("/Users/pennix/Downloads/inventory-1.18.xls")) {
			try (POIFSFileSystem poifs = new POIFSFileSystem(fin)) {
				DirectoryNode root = poifs.getRoot();
				Iterator<Entry> it = root.getEntries();
				while (it.hasNext()) {
					Entry entry = it.next();
					if (entry instanceof DocumentNode) {
						try (InputStream din = poifs.createDocumentInputStream("Workbook")) {
							// construct out HSSFRequest object
							HSSFRequest req = new HSSFRequest();
							// lazy listen for ALL records with the listener shown above
							req.addListenerForAllRecords(new EventExample());
							// create our event factory
							HSSFEventFactory factory = new HSSFEventFactory();
							// process our events based on the document input stream
							factory.processEvents(req, din);
						}
					}
				}
			}
		}
		//Workbook wb = WorkbookFactory.create(new File("/Users/pennix/Downloads/inventory-1.18.xls"));
		//fail("Not yet implemented");
	}

}
