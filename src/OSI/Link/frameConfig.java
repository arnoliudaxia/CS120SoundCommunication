package OSI.Link;

public class frameConfig {
    public static int headerLength;
    public static final int bitLength = 50;
    public static int fragmentLength;
    public static final float fragmentTime = 0.05f;
    //#region headerData (different length)
    public static final float[] header={0f,0.000765073859053316f,0.00154399520876823f,0.00233676404914474f,0.00314338038018286f,0.00396384420188257f,0.00479815551424387f,0.00564631431726678f,0.00650832061095129f,0.00738417439529739f,0.00827387567030509f,0.00917742443597440f,0.0100948206923053f,0.0110260644392978f,0.0119711556769519f,0.0129300944052676f,0.0139028806242449f,0.0148895143338838f,0.0158899955341843f,0.0169043242251463f,0.0179325004067700f,0.0189745240790553f,0.0200303952420022f,0.0211001138956107f,0.0221836800398808f,0.0232810936748125f,0.0243923548004057f,0.0255174634166606f,0.0266564195235771f,0.0278092231211552f,0.0289758742093948f,0.0301563727882961f,0.0313507188578590f,0.0325589124180834f,0.0337809534689695f,0.0350168420105172f,0.0362665780427264f,0.0375301615655973f,0.0388075925791297f,0.0400988710833238f,0.0414039970781795f,0.0427229705636967f,0.0440557915398756f,0.0454024600067160f,0.0467629759642181f,0.0481373394123817f,0.0495255503512070f,0.0509276087806938f,0.0523435147008423f,0.0537732681116523f,0.0552168690131240f,0.0566743174052572f,0.0581456132880520f,0.0596307566615085f,0.0611297475256265f,0.0626425858804061f,0.0641692717258474f,0.0657098050619502f,0.0672641858887146f,0.0688324142061407f,0.0704144900142283f,0.0720104133129775f,0.0736201841023883f,0.0752438023824608f,0.0768812681531948f,0.0785325814145904f,0.0801977421666476f,0.0818767504093664f,0.0835696061427469f,0.0852763093667889f,0.0869968600814925f,0.0887312582868577f,0.0904795039828845f,0.0922415971695729f,0.0940175378469229f,0.0958073260149345f,0.0976109616736077f,0.0994284448229425f,0.101259775462939f,0.103104953593597f,0.104963979214917f,0.106836852326898f,0.108723572929540f,0.110624141022845f,0.112538556606811f,0.114466819681438f,0.116408930246728f,0.118364888302678f,0.120334693849291f,0.122318346886565f,0.124315847414500f,0.126327195433098f,0.128352390942356f,0.130391433942277f,0.132444324432859f,0.134511062414102f,0.136591647886007f,0.138686080848574f,0.140794361301803f,0.142916489245693f,0.145052464680244f,0.147202287605457f,0.149365958021332f,0.151543475927868f,0.153734841325066f,0.155940054212926f,0.158159114591447f,0.160392022460630f,0.162638777820474f,0.164899380670980f,0.167173831012148f,0.169462128843977f,0.171764274166468f,0.174080266979620f,0.176410107283434f,0.178753795077909f,0.181111330363047f,0.183482713138845f,0.185867943405306f,0.188267021162428f,0.190679946410211f,0.193106719148656f,0.195547339377763f,0.198001807097531f,0.200470122307961f,0.202952285009053f,0.205448295200806f,0.207958152883221f,0.210481858056297f,0.213019410720035f,0.215570810874434f,0.218136058519496f,0.220715153655218f,0.223308096281603f,0.225914886398649f,0.228535524006356f,0.231170009104725f,0.233818341693756f,0.236480521773448f,0.239156549343802f,0.241846424404818f,0.244550146956495f,0.247267716998833f,0.249999134531834f,0.252744399555496f,0.255503512069819f,0.258276472074804f,0.261063279570451f,0.263863934556759f,0.266678437033729f,0.269506787001361f,0.272348984459654f,0.275205029408608f,0.278074921848225f,0.280958661778503f,0.283856249199442f,0.286767684111043f,0.289692966513306f,0.292632096406230f,0.295585073789816f,0.298551898664063f,0.301532571028972f,0.304527090884543f,0.307535458230775f,0.310557673067669f,0.313593735395225f,0.316643645213442f,0.319707402522320f,0.322785007321861f,0.325876459612063f,0.328981759392926f,0.332100906664451f,0.335233901426638f,0.338380743679486f,0.341541433422996f,0.344715970657167f,0.347904355382000f,0.351106587597495f,0.354322667303651f,0.357552594500469f,0.360796369187949f,0.364053991366090f,0.367325461034892f,0.370610778194356f,0.373909942844482f,0.377222954985270f,0.380549814616719f,0.383890521738829f,0.387245076351602f,0.390613478455036f,0.393995728049131f,0.397391825133888f,0.400801769709307f,0.404225561775387f,0.407663201332129f,0.411114688379532f,0.414580022917597f,0.418059204946324f,0.421552234465712f,0.425059111475762f,0.428579835976473f,0.432114407967846f,0.435662827449881f,0.439225094422577f,0.442801208885935f,0.446391170839954f,0.449994980284635f,0.453612637219978f,0.457244141645982f,0.460889493562648f,0.464548692969975f,0.468221739867964f,0.471908634256615f,0.475609376135927f,0.479323965505901f,0.483052402366536f,0.486794686717833f,0.490550818559792f,0.494320797892412f,0.498104624715694f,0.501895375284306f,0.505679202107588f,0.509449181440208f,0.513205313282167f,0.516947597633464f,0.520676034494099f,0.524390623864073f,0.528091365743385f,0.531778260132036f,0.535451307030025f,0.539110506437352f,0.542755858354018f,0.546387362780022f,0.550005019715365f,0.553608829160046f,0.557198791114065f,0.560774905577423f,0.564337172550119f,0.567885592032154f,0.571420164023527f,0.574940888524238f,0.578447765534288f,0.581940795053677f,0.585419977082403f,0.588885311620468f,0.592336798667872f,0.595774438224613f,0.599198230290694f,0.602608174866112f,0.606004271950869f,0.609386521544965f,0.612754923648399f,0.616109478261171f,0.619450185383281f,0.622777045014730f,0.626090057155518f,0.629389221805644f,0.632674538965108f,0.635946008633911f,0.639203630812052f,0.642447405499531f,0.645677332696349f,0.648893412402505f,0.652095644618000f,0.655284029342833f,0.658458566577004f,0.661619256320514f,0.664766098573362f,0.667899093335549f,0.671018240607074f,0.674123540387938f,0.677214992678139f,0.680292597477680f,0.683356354786558f,0.686406264604775f,0.689442326932331f,0.692464541769225f,0.695472909115457f,0.698467428971028f,0.701448101335937f,0.704414926210184f,0.707367903593770f,0.710307033486694f,0.713232315888957f,0.716143750800558f,0.719041338221498f,0.721925078151776f,0.724794970591392f,0.727651015540347f,0.730493212998640f,0.733321562966271f,0.736136065443241f,0.738936720429549f,0.741723527925196f,0.744496487930181f,0.747255600444505f,0.750000865468167f,0.752732283001167f,0.755449853043506f,0.758153575595183f,0.760843450656198f,0.763519478226552f,0.766181658306244f,0.768829990895275f,0.771464475993644f,0.774085113601352f,0.776691903718398f,0.779284846344782f,0.781863941480505f,0.784429189125566f,0.786980589279965f,0.789518141943703f,0.792041847116780f,0.794551704799194f,0.797047714990947f,0.799529877692039f,0.801998192902469f,0.804452660622237f,0.806893280851344f,0.809320053589789f,0.811732978837572f,0.814132056594695f,0.816517286861155f,0.818888669636954f,0.821246204922091f,0.823589892716566f,0.825919733020380f,0.828235725833532f,0.830537871156023f,0.832826168987853f,0.835100619329020f,0.837361222179526f,0.839607977539370f,0.841840885408553f,0.844059945787074f,0.846265158674934f,0.848456524072132f,0.850634041978668f,0.852797712394543f,0.854947535319756f,0.857083510754308f,0.859205638698198f,0.861313919151426f,0.863408352113993f,0.865488937585898f,0.867555675567142f,0.869608566057723f,0.871647609057644f,0.873672804566903f,0.875684152585500f,0.877681653113435f,0.879665306150709f,0.881635111697322f,0.883591069753273f,0.885533180318562f,0.887461443393189f,0.889375858977155f,0.891276427070460f,0.893163147673103f,0.895036020785084f,0.896895046406403f,0.898740224537061f,0.900571555177058f,0.902389038326392f,0.904192673985066f,0.905982462153077f,0.907758402830427f,0.909520496017116f,0.911268741713142f,0.913003139918508f,0.914723690633211f,0.916430393857253f,0.918123249590633f,0.919802257833353f,0.921467418585410f,0.923118731846805f,0.924756197617539f,0.926379815897612f,0.927989586687023f,0.929585509985772f,0.931167585793859f,0.932735814111286f,0.934290194938050f,0.935830728274153f,0.937357414119594f,0.938870252474374f,0.940369243338492f,0.941854386711948f,0.943325682594743f,0.944783130986876f,0.946226731888348f,0.947656485299158f,0.949072391219306f,0.950474449648793f,0.951862660587619f,0.953237024035782f,0.954597539993284f,0.955944208460125f,0.957277029436303f,0.958596002921821f,0.959901128916676f,0.961192407420870f,0.962469838434403f,0.963733421957274f,0.964983157989483f,0.966219046531031f,0.967441087581917f,0.968649281142141f,0.969843627211704f,0.971024125790605f,0.972190776878845f,0.973343580476423f,0.974482536583340f,0.975607645199594f,0.976718906325188f,0.977816319960119f,0.978899886104389f,0.979969604757998f,0.981025475920945f,0.982067499593230f,0.983095675774854f,0.984110004465816f,0.985110485666116f,0.986097119375755f,0.987069905594733f,0.988028844323048f,0.988973935560702f,0.989905179307695f,0.990822575564026f,0.991726124329695f,0.992615825604703f,0.993491679389049f,0.994353685682733f,0.995201844485756f,0.996036155798117f,0.996856619619817f,0.997663235950855f,0.998456004791232f,0.999234926140947f,1.f};
}
