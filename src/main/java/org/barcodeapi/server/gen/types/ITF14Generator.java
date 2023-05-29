package org.barcodeapi.server.gen.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.barcodeapi.server.gen.CodeGenerator;
import org.barcodeapi.server.gen.CodeType;
import org.json.JSONObject;
import org.krysalis.barcode4j.HumanReadablePlacement;
import org.krysalis.barcode4j.impl.int2of5.ITF14Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

public class ITF14Generator extends CodeGenerator {

	private ITF14Bean generator;

	/**
	 * Constructor for the ITF-14 generator.
	 */
	public ITF14Generator() {
		super(CodeType.ITF14);

		// Setup ITF-14 generator
		generator = new ITF14Bean();
	}

	@Override
	public byte[] onRender(String data, JSONObject options) throws IOException {

		int dpi = options.optInt("dpi", 150);
		double moduleWidth = UnitConv.in2mm(2.5f / dpi);

		double qz = options.optDouble("qz", (10 * moduleWidth));
		int height = options.optInt("height", 25);

		String text = options.optString("text", "bottom");
		String pattern = options.optString("pattern", "_ __ _____ _____ _");

		synchronized (generator) {

			switch (text) {

			case "bottom":
				generator.setMsgPosition(HumanReadablePlacement.HRP_BOTTOM);
				break;

			case "top":
				generator.setMsgPosition(HumanReadablePlacement.HRP_TOP);
				break;

			case "none":
			default:
				generator.setMsgPosition(HumanReadablePlacement.HRP_NONE);
				break;
			}

			generator.doQuietZone(true);
			generator.setQuietZone(qz);
			generator.setHeight(height);
			generator.setModuleWidth(moduleWidth);

			generator.setPattern(pattern);
			generator.setFontSize(12 * moduleWidth);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BitmapCanvasProvider canvasProvider = new BitmapCanvasProvider(//
					out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

			generator.generateBarcode(canvasProvider, data);
			canvasProvider.finish();
			out.close();

			return out.toByteArray();
		}
	}
}
