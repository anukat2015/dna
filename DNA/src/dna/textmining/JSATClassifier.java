package dna.textmining;


/**
 * Do NOT use this classifier. Something is strange because the classifier is never able to make correct predictions! 
 *
 */

public class JSATClassifier{}

//public class JSATClassifier extends DNAClassifier implements Serializable {

//	private static final long serialVersionUID = -5352005109028350668L;
//	private Classifier clf;
//	private ClassificationDataSet trainSet;
//	private boolean upToDate;
//	private DataTransform dt;
//
//	public JSATClassifier( Classifier clf ) {
//		this.clf = clf;
//	}
//
//	@Override
//	public void updateData(SparseVector sparseVector, String classValue) {
//		double[] vec = sparseVector.toArray();
//		if ( trainSet == null ) {
//			trainSet = new ClassificationDataSet(vec.length, new CategoricalData[0], new CategoricalData(2));
//		}
//
//		Vec v = new SparseVector( Utils.asList(vec) );
//
//		int classification = 0;
//
//		if ( classValue.equals( DNAClassifier.POSITIVE_CLASS ) ) {
//			classification = 1;
//		}
//
//		trainSet.addDataPoint(v, new int[0], classification);
//
//		upToDate = false;
//	}
//
//	@Override
//	public void updateClassifier() {
//		if ( !upToDate ) {
//			System.out.println( "Training classifier ... " );
//			dt = new LinearTransform(trainSet);
//			trainSet.applyTransform(dt);
//			System.out.println( trainSet.getSampleSize() );
//			clf.trainC(trainSet);
//			upToDate = true;
//		}
//	}
//
//	@Override
//	public String classifyInstance(double[] vec) {
//		if ( !upToDate ) {
//			updateClassifier();
//		}
//		Vec v = new SparseVector( Utils.asList(vec) );
//		DataPoint p = new DataPoint(v);
//		p = dt.transform(p);
//		CategoricalResults result = clf.classify(p);
//		int cat = result.mostLikely();
//		if ( cat == 1 )
//			return DNAClassifier.POSITIVE_CLASS;
//		else 
//			return DNAClassifier.NEGATIVE_CLASS;
//	}
//
//	@Override
//	public Map<String, Double> distributionForInstance(double vec[]) {
//		if ( !upToDate ) {
//			updateClassifier();
//		}
//		
//		Vec v = new SparseVector( Utils.asList(vec) );
//		DataPoint p = new DataPoint(v);
//		p = dt.transform(p);
//		CategoricalResults result = clf.classify(p);
//		Map<String, Double> results = new HashMap<>();
//		results.put( DNAClassifier.POSITIVE_CLASS, result.getProb(1) );
//		results.put( DNAClassifier.NEGATIVE_CLASS, result.getProb(0) );
//		return results;
//	}
//
//	@Override
//	public void save() {
//		try {
//			SerializationHelper.write("JSAT_CLF", this);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}