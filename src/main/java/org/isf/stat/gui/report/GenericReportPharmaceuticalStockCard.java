/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.stat.gui.report;

import static org.isf.utils.Constants.DATE_FORMAT_YYYYMMDD;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.isf.generaldata.MessageBundle;
import org.isf.medicals.model.Medical;
import org.isf.menu.manager.Context;
import org.isf.stat.dto.JasperReportResultDto;
import org.isf.stat.manager.JasperReportsManager;
import org.isf.utils.excel.ExcelExporter;
import org.isf.utils.jobjects.MessageDialog;
import org.isf.ward.model.Ward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created on 15/Jun/08
 */
public class GenericReportPharmaceuticalStockCard extends DisplayReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericReportPharmaceuticalStockCard.class);
	private JasperReportsManager jasperReportsManager = Context.getApplicationContext().getBean(JasperReportsManager.class);

	public GenericReportPharmaceuticalStockCard(String jasperFileName, LocalDateTime dateFrom, LocalDateTime dateTo, Medical medical, Ward ward,
			boolean toExcel) {
		if (dateFrom == null || dateTo == null) {
			return;
		}
		try {
			File defaultFilename = new File(compileStockCardFilename(jasperFileName, dateFrom, dateTo, medical, ward));

			if (toExcel) {
				JFileChooser fcExcel = ExcelExporter.getJFileChooserExcel(defaultFilename);
				int iRetVal = fcExcel.showSaveDialog(null);
				if (iRetVal == JFileChooser.APPROVE_OPTION) {
					File exportFile = fcExcel.getSelectedFile();
					FileNameExtensionFilter selectedFilter = (FileNameExtensionFilter) fcExcel.getFileFilter();
					String extension = selectedFilter.getExtensions()[0];
					if (!exportFile.getName().endsWith(extension)) {
						exportFile = new File(exportFile.getAbsoluteFile() + "." + extension);
					}
					jasperReportsManager.getGenericReportPharmaceuticalStockCardExcel(jasperFileName, exportFile.getAbsolutePath(), dateFrom, dateTo, medical,
							ward);
				}
			} else {
				JasperReportResultDto jasperReportResultDto = jasperReportsManager.getGenericReportPharmaceuticalStockCardPdf(jasperFileName,
						defaultFilename.getName(), dateFrom, dateTo, medical, ward);
				showReport(jasperReportResultDto);
			}
		} catch (Exception e) {
			LOGGER.error("", e);
			MessageDialog.error(null, "angal.stat.reporterror.msg");
		}
	}

	private String compileStockCardFilename(String jasperFileName, LocalDateTime dateFrom, LocalDateTime dateTo, Medical medical, Ward ward) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMMDD);
		StringBuilder fileName = new StringBuilder(jasperFileName);
		if (ward != null) {
			fileName.append('_').append(ward.getDescription());
		}
		if (medical != null) {
			fileName.append('_').append(medical.getCode());
		}
		fileName.append('_').append(MessageBundle.getMessage("angal.common.from.txt"))
				.append('_').append(dateTimeFormatter.format(dateFrom))
				.append('_').append(MessageBundle.getMessage("angal.common.to.txt"))
				.append('_').append(dateTimeFormatter.format(dateTo));
		return fileName.toString();
	}

}
