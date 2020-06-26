package de.bausdorf.simcacing.tt.live.model.client;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import de.bausdorf.simcacing.tt.live.clientapi.ClientData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TyreData implements ClientData {
	double wearLFO;
	double wearLFM;
	double wearLFI;
	double wearRFO;
	double wearRFM;
	double wearRFI;
	double wearLRO;
	double wearLRM;
	double wearLRI;
	double wearRRO;
	double wearRRM;
	double wearRRI;

	public List<String> getOuterWear() {
		return Arrays.asList(
				String.format(Locale.US,"%.0f",wearLFO * 100),
				String.format(Locale.US,"%.0f",wearRFO * 100),
				String.format(Locale.US,"%.0f",wearRRO * 100),
				String.format(Locale.US,"%.0f",wearLRO * 100));
	}

	public List<String> getMiddleWear() {
		return Arrays.asList(
				String.format(Locale.US,"%.0f",wearLFM * 100),
				String.format(Locale.US,"%.0f",wearRFM * 100),
				String.format(Locale.US,"%.0f",wearRRM * 100),
				String.format(Locale.US,"%.0f",wearLRM * 100));
	}

	public List<String> getInnerWear() {
		return Arrays.asList(
				String.format(Locale.US, "%.0f", wearLFI * 100),
				String.format(Locale.US, "%.0f", wearRFI * 100),
				String.format(Locale.US, "%.0f", wearRRI * 100),
				String.format(Locale.US, "%.0f", wearLRI * 100));
	}
}
