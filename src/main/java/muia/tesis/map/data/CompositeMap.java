package muia.tesis.map.data;

public class CompositeMap {

	private HighLevelMap hl;
	private LowLevelMap[] lls;

	//TODO: cambiar nombre por algo que no de conflicto
	public CompositeMap(HighLevelMap hl, LowLevelMap[] lls) {
		this.hl = hl;
		this.lls = lls;
	}
	
	public HighLevelMap getHLMap() {
		return this.hl;
	}
	
	public LowLevelMap[] getLLMaps() {
		return this.lls;
	}

	@Override
	public String toString() {
		String s = "[CompositeMap]\n\t" + this.hl;
		for (LowLevelMap ll : this.lls)
			s += "\n\t" + ll;
		return s;
	}
}
