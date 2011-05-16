package erwins.util.tools;

/** 구글에서 주워온거 */
public class WGS84 {
	
	public WGS84(double latitude,double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	private final double  latitude;
	private final double  longitude;
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	
    /**
     * Equatorial radius of earth is required for distance computation.
     */
    public static final double EQUATORIALRADIUS = 6378137.0;

    /**
     * Polar radius of earth is required for distance computation.
     */
    public static final double POLARRADIUS = 6356752.3142;

    /**
     * The flattening factor of the earth's ellipsoid is required for distance computation.
     */
    public static final double INVERSEFLATTENING = 298.257223563;

    /**
     * Calculates geodetic distance between two GeoCoordinates using Vincenty inverse formula
     * for ellipsoids. This is very accurate but consumes more resources and time than the
     * sphericalDistance method
     * 
     * Adaptation of Chriss Veness' JavaScript Code on
     * http://www.movable-type.co.uk/scripts/latlong-vincenty.html
     * 
     * Paper: Vincenty inverse formula - T Vincenty, "Direct and Inverse Solutions of Geodesics
     * on the Ellipsoid with application of nested equations", Survey Review, vol XXII no 176,
     * 1975 (http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf)
     * 
     * @param gc1
     *            first GeoCoordinate
     * @param gc2
     *            second GeoCoordinate
     * 
     * @return distance in meters between points as a double
     */
    public static double vincentyDistance(WGS84 gc1, WGS84 gc2) {
            double f = 1 / INVERSEFLATTENING;
            double L = Math.toRadians(gc2.getLongitude() - gc1.getLongitude());
            double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(gc1.getLatitude())));
            double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(gc2.getLatitude())));
            double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
            double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

            double lambda = L, lambdaP, iterLimit = 100;

            double cosSqAlpha = 0, sinSigma = 0, cosSigma = 0, cos2SigmaM = 0, sigma = 0, sinLambda = 0, sinAlpha = 0, cosLambda = 0;
            do {
                    sinLambda = Math.sin(lambda);
                    cosLambda = Math.cos(lambda);
                    sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                                    * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
                    if (sinSigma == 0)
                            return 0; // co-incident points
                    cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
                    sigma = Math.atan2(sinSigma, cosSigma);
                    sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
                    cosSqAlpha = 1 - sinAlpha * sinAlpha;
                    if (cosSqAlpha != 0) {
                            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
                    } else {
                            cos2SigmaM = 0;
                    }
                    double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
                    lambdaP = lambda;
                    lambda = L
                                    + (1 - C)
                                    * f
                                    * sinAlpha
                                    * (sigma + C * sinSigma
                                                    * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
            } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

            if (iterLimit == 0)
                    return 0; // formula failed to converge

            double uSq = cosSqAlpha * (Math.pow(EQUATORIALRADIUS, 2) - Math.pow(POLARRADIUS, 2))
                            / Math.pow(POLARRADIUS, 2);
            double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
            double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
            double deltaSigma = B
                            * sinSigma
                            * (cos2SigmaM + B
                                            / 4
                                            * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                                                            * (-3 + 4 * sinSigma * sinSigma)
                                                            * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
            double s = POLARRADIUS * A * (sigma - deltaSigma);

            return s;
    }


}
