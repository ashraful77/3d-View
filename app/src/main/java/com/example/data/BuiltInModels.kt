package com.example.data

import com.example.domain.AnimationMetadata
import com.example.domain.Capability
import com.example.domain.Category
import com.example.domain.EducationalInfo
import com.example.domain.ModelDimensions
import com.example.domain.ModelLabel
import com.example.domain.ModelObject3D
import com.example.domain.ModelPart
import com.example.domain.Subject
import com.example.domain.SubjectId
import com.example.ui.theme.SubjectBiology
import com.example.ui.theme.SubjectChemistry
import com.example.ui.theme.SubjectMath
import com.example.ui.theme.SubjectOthers
import com.example.ui.theme.SubjectPhysics
import com.example.viewer3d.model.Mesh3D
import com.example.viewer3d.model.ShapeGenerators

object BuiltInModels {

    // ==========================================
    // 1. CUBE (Mathematics)
    // ==========================================
    val CUBE = ModelObject3D(
        id = "cube",
        name = "Cube (Regular Hexahedron)",
        subjectId = SubjectId.MATHEMATICS,
        categoryId = "solid_geometry",
        description = "A symmetrical three-dimensional solid with 6 square faces, 12 equal edges, and 8 vertices.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS,
            Capability.NET_UNFOLD
        ),
        parts = listOf(
            ModelPart("top_face", "Top Face", "Upper square face parallel to the base.", "Highlights how opposite faces in a hexahedron are congruent and parallel.", 0xFF7DD3FC, "Area = a²"),
            ModelPart("bottom_face", "Bottom Base", "Horizontal base face supporting the cube.", "Forms the reference plane when calculating height and base area.", 0xFF38BDF8, "Area = a²"),
            ModelPart("front_face", "Front Face", "Anterior vertical face perpendicular to bottom base.", "Demonstrates the 90° dihedral angle between adjacent faces.", 0xFF0284C7, "Area = a²"),
            ModelPart("back_face", "Back Face", "Posterior vertical face opposite the front face.", "Anchor facet when unfolding into a standard cross-shaped planar net.", 0xFF0369A1, "Area = a²"),
            ModelPart("left_face", "Left Face", "Lateral vertical face on the negative X-axis.", "Opposite and parallel to the right face.", 0xFF0EA5E9, "Area = a²"),
            ModelPart("right_face", "Right Face", "Lateral vertical face on the positive X-axis.", "Completes the 4 vertical lateral boundary walls.", 0xFF38BDF8, "Area = a²")
        ),
        labels = listOf(
            ModelLabel("lbl_top", "Top Face", "top_face", 0f, 1.15f, 0f),
            ModelLabel("lbl_front", "Front Face", "front_face", 0f, 0f, 1.15f),
            ModelLabel("lbl_right", "Right Face", "right_face", 1.15f, 0f, 0f),
            ModelLabel("lbl_bottom", "Base", "bottom_face", 0f, -1.15f, 0f)
        ),
        dimensions = ModelDimensions(
            title = "Cube Metrics (Side a = 5 cm)",
            formulas = listOf(
                "Volume (V)" to "V = a³",
                "Total Surface Area (TSA)" to "TSA = 6a²",
                "Lateral Surface Area (LSA)" to "LSA = 4a²",
                "Space Diagonal (d)" to "d = a√3 ≈ 8.66 cm"
            ),
            sampleMeasurements = listOf(
                "Side length (a)" to "5.0 cm",
                "Computed Volume" to "125.0 cm³",
                "Computed Total Surface" to "150.0 cm²"
            )
        ),
        animationInfo = AnimationMetadata(
            hasAnimation = true,
            name = "2D Net Unfolding",
            description = "Unfolds the 3D solid cube into a flat 2D cross-shaped pattern.",
            canPlay = false
        ),
        educationalInfo = EducationalInfo(
            definition = "A cube is a regular convex polyhedron bounded by six congruent square faces, with three faces meeting at each of the eight vertices.",
            keyPoints = listOf(
                "Has 6 identical square faces, 12 edges of equal length, and 8 vertices.",
                "Every face meets adjacent faces at a perpendicular 90° angle (orthogonal).",
                "Euler's Polyhedral Formula holds true: V - E + F = 8 - 12 + 6 = 2.",
                "Unfolding along edges generates exactly 11 distinct possible planar nets."
            ),
            realWorldApplications = listOf(
                "Packaging & shipping box standardization for maximum packing efficiency.",
                "Crystalline mineral structures such as table salt (NaCl / halite crystal lattice).",
                "3D digital voxels in medical imaging (CT/MRI volumetric reconstructions)."
            ),
            teacherTips = listOf(
                "Slide the '2D Net Unfold' slider in class so students can see which square maps to which 3D face.",
                "Ask students to count the vertices (8) and edges (12) by rotating the model.",
                "Contrast the length of a face diagonal (a√2) with the space internal diagonal (a√3)."
            ),
            discussionQuestions = listOf(
                "If we double the edge length of a cube, by what factor does its surface area increase? What about its volume?",
                "Why does a table salt crystal naturally cleave into cubic shapes at the atomic level?"
            )
        ),
        classLevel = "Grades 6–10",
        tags = listOf("Geometry", "Platonic Solid", "Polyhedron", "Net", "Surface Area")
    )

    // ==========================================
    // 2. CYLINDER (Mathematics)
    // ==========================================
    val CYLINDER = ModelObject3D(
        id = "cylinder",
        name = "Right Circular Cylinder",
        subjectId = SubjectId.MATHEMATICS,
        categoryId = "solid_geometry",
        description = "A curvilinear 3D geometric solid formed by rotating a rectangle around one of its sides.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS,
            Capability.NET_UNFOLD, Capability.CUTAWAY_XRAY
        ),
        parts = listOf(
            ModelPart("top_base", "Top Circular Base", "Planar circular boundary disk at height +h/2.", "Congruent and parallel to the bottom circular base.", 0xFF38BDF8, "Area = πr²"),
            ModelPart("bottom_base", "Bottom Circular Base", "Planar circular foundation disk at height -h/2.", "Forms the cross-sectional foundation parallel to horizontal plane.", 0xFF0369A1, "Area = πr²"),
            ModelPart("curved_surface", "Curved Lateral Surface", "Continuous curved mantle connecting the two circular bases.", "When unrolled flat, forms a rectangle of width 2πr and height h.", 0xFF0284C7, "Area = 2πrh")
        ),
        labels = listOf(
            ModelLabel("lbl_top_base", "Top Base (πr²)", "top_base", 0f, 1.35f, 0f),
            ModelLabel("lbl_mantle", "Curved Surface (2πrh)", "curved_surface", 1.35f, 0f, 0f),
            ModelLabel("lbl_bot_base", "Bottom Base (πr²)", "bottom_base", 0f, -1.35f, 0f)
        ),
        dimensions = ModelDimensions(
            title = "Cylinder Metrics (r = 4 cm, h = 10 cm)",
            formulas = listOf(
                "Volume (V)" to "V = πr²h",
                "Curved Surface Area (CSA)" to "CSA = 2πrh",
                "Total Surface Area (TSA)" to "TSA = 2πr(r + h) = 2πrh + 2πr²",
                "Base Circumference (C)" to "C = 2πr ≈ 25.13 cm"
            ),
            sampleMeasurements = listOf(
                "Base Radius (r)" to "4.0 cm",
                "Height (h)" to "10.0 cm",
                "Computed Volume" to "502.65 cm³",
                "Total Surface Area" to "351.86 cm²"
            )
        ),
        animationInfo = AnimationMetadata(
            hasAnimation = true,
            name = "Lateral Mantle Unrolling",
            description = "Unrolls the curved lateral wall into a flat rectangle with two circular base lids.",
            canPlay = false
        ),
        educationalInfo = EducationalInfo(
            definition = "A right circular cylinder is a three-dimensional solid bounded by two parallel congruent circular disks and a smooth lateral surface formed by line segments parallel to the central axis.",
            keyPoints = listOf(
                "Composed of 2 flat circular bases and 1 curved lateral surface.",
                "Has no sharp vertices; contains 2 smooth curved circular boundary edges.",
                "The cross-section parallel to the base is always an identical circle of radius r.",
                "The unrolled lateral net is a flat rectangle of dimensions (2πr) × h."
            ),
            realWorldApplications = listOf(
                "Industrial storage silos, pipelines, and drink cans (cylinders optimize pressure distribution).",
                "Automotive internal combustion engine cylinders and hydraulic pistons.",
                "Architectural columns designed for high vertical load-bearing capacity."
            ),
            teacherTips = listOf(
                "Use the '2D Net Unfold' tool to visually prove to students why the formula for CSA is 2πrh (rectangle width is the circle perimeter 2πr).",
                "Switch to 'Cutaway / X-Ray' mode to display the central axis connecting the centers of both circles."
            ),
            discussionQuestions = listOf(
                "Why are beverage cans shaped as cylinders rather than cubes or rectangular boxes?",
                "If we keep the volume constant, what ratio of radius to height minimizes the metal required for the can?"
            )
        ),
        classLevel = "Grades 7–10",
        tags = listOf("Geometry", "Cylinder", "Mensuration", "Calculus", "Volume")
    )

    // ==========================================
    // 3. CONE (Mathematics)
    // ==========================================
    val CONE = ModelObject3D(
        id = "cone",
        name = "Right Circular Cone",
        subjectId = SubjectId.MATHEMATICS,
        categoryId = "solid_geometry",
        description = "A 3D shape tapering smoothly from a flat circular base to a sharp point called the apex.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS,
            Capability.NET_UNFOLD, Capability.CUTAWAY_XRAY
        ),
        parts = listOf(
            ModelPart("lateral_surface", "Conical Lateral Surface", "Tapered curved surface extending from circular boundary to the apex.", "Unrolls into a circular sector of radius equal to the slant height l.", 0xFF0EA5E9, "Area = πrl"),
            ModelPart("base_disk", "Circular Base Disk", "Flat circular base at the bottom of the cone.", "Reference plane with radius r.", 0xFF0284C7, "Area = πr²")
        ),
        labels = listOf(
            ModelLabel("lbl_apex", "Apex / Vertex", "lateral_surface", 0f, 1.4f, 0f),
            ModelLabel("lbl_slant", "Slant Height (l)", "lateral_surface", 0.9f, 0.2f, 0f),
            ModelLabel("lbl_base", "Circular Base (r)", "base_disk", 0f, -1.35f, 0f)
        ),
        dimensions = ModelDimensions(
            title = "Cone Metrics (r = 3 cm, h = 7 cm)",
            formulas = listOf(
                "Volume (V)" to "V = (1/3)πr²h",
                "Slant Height (l)" to "l = √(r² + h²) ≈ 7.62 cm",
                "Curved Surface Area (CSA)" to "CSA = πrl",
                "Total Surface Area (TSA)" to "TSA = πr(l + r) = πrl + πr²"
            ),
            sampleMeasurements = listOf(
                "Base Radius (r)" to "3.0 cm",
                "Height (h)" to "7.0 cm",
                "Slant Height (l)" to "7.62 cm",
                "Computed Volume" to "65.97 cm³"
            )
        ),
        animationInfo = AnimationMetadata(
            hasAnimation = true,
            name = "Conical Sector Unfolding",
            description = "Flattens the cone's curved surface into a 2D planar sector and separates the base disk.",
            canPlay = false
        ),
        educationalInfo = EducationalInfo(
            definition = "A right circular cone is a three-dimensional geometric shape with a circular base and a single vertex (apex) directly above the center of the base.",
            keyPoints = listOf(
                "The volume of a cone is exactly 1/3 the volume of a cylinder with identical base radius and height.",
                "Slant height (l), vertical height (h), and radius (r) form a right-angled triangle: l² = r² + h².",
                "When sliced by a plane, cones produce conic sections: circle, ellipse, parabola, and hyperbola.",
                "The lateral net is a circular sector with arc length equal to base circumference 2πr."
            ),
            realWorldApplications = listOf(
                "Traffic safety cones designed for high wind stability and stackability.",
                "Speaker cone diaphragms for acoustic sound wave dispersion.",
                "Volcanic cinder cones and hourglass gravitational flow funnels."
            ),
            teacherTips = listOf(
                "Demonstrate the relationship between a cylinder and a cone: show that 3 filled cones of water exactly fill one cylinder of identical height and radius.",
                "Use the Dimensions mode to point out the Pythagorean relationship (r, h, l)."
            ),
            discussionQuestions = listOf(
                "Why is the volume formula for a cone exactly one-third of the cylinder formula?",
                "What shape do you get if you slice a cone parallel to its slant edge?"
            )
        ),
        classLevel = "Grades 8–10",
        tags = listOf("Geometry", "Cone", "Conic Sections", "Pythagoras", "Mensuration")
    )

    // ==========================================
    // 4. SPHERE (Mathematics)
    // ==========================================
    val SPHERE = ModelObject3D(
        id = "sphere",
        name = "Sphere (Geometric Solid)",
        subjectId = SubjectId.MATHEMATICS,
        categoryId = "solid_geometry",
        description = "A perfectly symmetrical round 3D solid where every surface point is equidistant from the center.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS,
            Capability.CUTAWAY_XRAY
        ),
        parts = listOf(
            ModelPart("northern_hemisphere", "Northern Hemisphere", "Upper half of the sphere above the equatorial plane.", "Symmetrical to the southern half across the equatorial great circle.", 0xFF38BDF8, "Area = 2πr²"),
            ModelPart("southern_hemisphere", "Southern Hemisphere", "Lower half of the sphere below the equatorial plane.", "Together with the northern half, forms the complete closed spherical surface.", 0xFF0284C7, "Area = 2πr²"),
            ModelPart("equator_circle", "Great Circle / Equator", "Maximum circumference circle whose plane passes directly through the sphere's center.", "Shortest path between any two points on a sphere is along a great circle arc.", 0xFFF59E0B, "Circumference = 2πr")
        ),
        labels = listOf(
            ModelLabel("lbl_north", "North Pole", "northern_hemisphere", 0f, 1.55f, 0f),
            ModelLabel("lbl_equator", "Great Circle Equator", "equator_circle", 1.55f, 0f, 0f),
            ModelLabel("lbl_south", "South Pole", "southern_hemisphere", 0f, -1.55f, 0f)
        ),
        dimensions = ModelDimensions(
            title = "Sphere Metrics (Radius r = 4 cm)",
            formulas = listOf(
                "Volume (V)" to "V = (4/3)πr³",
                "Surface Area (A)" to "A = 4πr²",
                "Great Circle Area (A_gc)" to "A_gc = πr² (exactly 1/4 of total surface!)",
                "Great Circle Circumference" to "C = 2πr ≈ 25.13 cm"
            ),
            sampleMeasurements = listOf(
                "Radius (r)" to "4.0 cm",
                "Diameter (d)" to "8.0 cm",
                "Computed Volume" to "268.08 cm³",
                "Computed Surface Area" to "201.06 cm²"
            )
        ),
        animationInfo = null,
        educationalInfo = EducationalInfo(
            definition = "A sphere is the set of all points in three-dimensional space that are at a given distance r (radius) from a given point (center).",
            keyPoints = listOf(
                "Possesses the highest symmetry of any 3D geometric shape (infinitely many planes of symmetry).",
                "Among all solids with a given volume, the sphere has the minimum surface area (isoperimetric quotient).",
                "Archimedes' Hat-Box Theorem: The area of a sphere equals the lateral surface area of its circumscribed cylinder (4πr²).",
                "Great circle routes represent geodesics (shortest navigational flight distances on Earth)."
            ),
            realWorldApplications = listOf(
                "Planetary astrophysics: Celestial bodies collapse into spheres under hydrostatic equilibrium.",
                "Surface tension in liquid soap bubbles and raindrops minimizing surface energy.",
                "High-pressure gas storage tanks (spherical shells distribute interior tensile stress evenly)."
            ),
            teacherTips = listOf(
                "Point out the Archimedean beauty: the surface area of a sphere is exactly four times the area of its great circle (A = 4 × πr²).",
                "Use the Great Circle highlight to explain longitude, latitude, and great circle airplane flight routes."
            ),
            discussionQuestions = listOf(
                "Why do soap bubbles naturally form spheres rather than cubes or cylinders?",
                "Can a sphere be unfolded into a flat 2D plane without stretching or tearing? (Mercator distortion)."
            )
        ),
        classLevel = "Grades 7–12",
        tags = listOf("Geometry", "Sphere", "Archimedes", "Symmetry", "Volume")
    )

    // ==========================================
    // 5. HUMAN HEART (Biology)
    // ==========================================
    val HUMAN_HEART = ModelObject3D(
        id = "heart",
        name = "Human Heart (Cardiovascular Anatomy)",
        subjectId = SubjectId.BIOLOGY,
        categoryId = "human_body",
        description = "Muscular organ responsible for pumping oxygenated and deoxygenated blood through the circulatory system.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS,
            Capability.ANIMATION, Capability.CUTAWAY_XRAY
        ),
        parts = listOf(
            ModelPart("left_ventricle", "Left Ventricle", "Thick muscular lower-left pumping chamber.", "Generates the high pressure (~120 mmHg) required to pump oxygen-rich blood throughout the entire body via the aorta.", 0xFFDC2626, "Systemic High Pressure (~120 mmHg)"),
            ModelPart("right_ventricle", "Right Ventricle", "Anterior lower chamber with thinner muscular wall.", "Pumps deoxygenated blood to the lungs through the pulmonary artery under low pressure (~25 mmHg).", 0xFFB91C1C, "Pulmonary Circulation (~25 mmHg)"),
            ModelPart("left_atrium", "Left Atrium", "Upper-left receiving chamber.", "Receives freshly oxygenated blood from the lungs via four pulmonary veins and passes it to the left ventricle.", 0xFFE11D48, "Receives oxygenated blood"),
            ModelPart("right_atrium", "Right Atrium", "Upper-right receiving chamber.", "Receives deoxygenated venous blood returning from systemic body tissues via the venae cavae.", 0xFF1E40AF, "Receives deoxygenated blood"),
            ModelPart("aorta", "Aortic Arch", "Largest systemic artery in the human body.", "Carries high-pressure oxygenated blood from the left ventricle into systemic circulation, branching into head/arm arteries.", 0xFFEF4444, "Main Systemic Artery (~2.5 cm diameter)"),
            ModelPart("pulmonary_artery", "Pulmonary Artery", "Artery carrying deoxygenated blood toward lungs.", "The only major artery in the post-natal human body carrying oxygen-depleted venous blood.", 0xFF2563EB, "To Pulmonary Capillaries"),
            ModelPart("superior_vena_cava", "Superior Vena Cava", "Large vein returning deoxygenated blood from upper body.", "Drains venous blood from the head, neck, and upper limbs directly into the right atrium.", 0xFF1D4ED8, "Major Systemic Vein"),
            ModelPart("septum", "Interventricular Septum", "Thick muscular dividing wall separating left and right ventricles.", "Prevents mixing of oxygenated blood on the left with deoxygenated blood on the right side.", 0xFF991B1B, "Anatomical Divider")
        ),
        labels = listOf(
            ModelLabel("lbl_aorta", "Aortic Arch", "aorta", -0.1f, 1.35f, -0.1f),
            ModelLabel("lbl_pulm", "Pulmonary Trunk", "pulmonary_artery", 0.35f, 1.05f, 0.1f),
            ModelLabel("lbl_vencav", "Superior Vena Cava", "superior_vena_cava", -0.7f, 1.25f, 0f),
            ModelLabel("lbl_lv", "Left Ventricle", "left_ventricle", 0.65f, -0.55f, 0.15f),
            ModelLabel("lbl_rv", "Right Ventricle", "right_ventricle", -0.65f, -0.45f, 0.25f),
            ModelLabel("lbl_la", "Left Atrium", "left_atrium", 0.65f, 0.55f, -0.15f),
            ModelLabel("lbl_ra", "Right Atrium", "right_atrium", -0.75f, 0.5f, 0.1f)
        ),
        dimensions = ModelDimensions(
            title = "Cardiovascular Parameters",
            formulas = listOf(
                "Cardiac Output (CO)" to "CO = Heart Rate (HR) × Stroke Volume (SV)",
                "Average Resting Output" to "70 bpm × 70 mL ≈ 5.0 Liters/min",
                "Blood Flow Pathway" to "Body → Vena Cava → RA → RV → Lungs → LA → LV → Aorta → Body",
                "Resting Blood Pressure" to "120/80 mmHg (Systolic / Diastolic)"
            ),
            sampleMeasurements = listOf(
                "Heart Mass" to "approx. 250–350 grams",
                "Typical Adult Length" to "approx. 12 cm",
                "Contractions per Day" to "~100,000 beats"
            )
        ),
        animationInfo = AnimationMetadata(
            hasAnimation = true,
            name = "Cardiac Cycle Pulsation",
            description = "Simulates synchronized atrial and ventricular systole and diastole contractions.",
            canPlay = true,
            defaultBpm = 72
        ),
        educationalInfo = EducationalInfo(
            definition = "The human heart is a four-chambered muscular pump located in the middle mediastinum that drives the systemic and pulmonary circulatory systems.",
            keyPoints = listOf(
                "The heart has two independent circulatory circuits: Pulmonary (to lungs) and Systemic (to rest of body).",
                "Double Circulation: Blood passes through the heart twice for every complete circuit around the body.",
                "Left ventricle wall is 3× thicker than the right ventricle wall because it must pump against systemic peripheral resistance.",
                "Sinoatrial (SA) node acts as the natural pacemaker generating electrical action potentials."
            ),
            realWorldApplications = listOf(
                "Clinical cardiology: Electrocardiography (ECG/EKG) diagnosis of cardiac arrhythmias.",
                "Echocardiogram 3D ultrasound visualization of heart valve regurgitation.",
                "Treatment of coronary artery disease through angioplasty, stenting, and bypass surgery."
            ),
            teacherTips = listOf(
                "Tap each chamber in order to trace the complete path of a red blood cell through the double circulation loop.",
                "Turn on 'Animation' and adjust the BPM slider between 60 (resting athlete) and 120 (aerobic exercise) to illustrate heart rate dynamics.",
                "Highlight the Interventricular Septum and explain congenital septal defects ('hole in the heart')."
            ),
            discussionQuestions = listOf(
                "Why is the wall of the left ventricle significantly thicker than that of the right ventricle?",
                "What would happen if the interventricular septum did not close properly during embryonic development?"
            )
        ),
        classLevel = "Grades 8–12",
        tags = listOf("Biology", "Circulation", "Anatomy", "Heart", "Physiology", "Double Circulation")
    )

    // ==========================================
    // 6. WATER MOLECULE (Chemistry)
    // ==========================================
    val WATER_MOLECULE = ModelObject3D(
        id = "water_molecule",
        name = "Water Molecule (H₂O)",
        subjectId = SubjectId.CHEMISTRY,
        categoryId = "molecular_structure",
        description = "VSEPR bent molecular geometry with polar covalent bonds and partial electric dipole moment.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS
        ),
        parts = listOf(
            ModelPart("oxygen_atom", "Oxygen Atom (O)", "Central electronegative atom with 8 protons and 2 lone pairs.", "High electronegativity pulls bonding electron density towards itself, creating partial negative charge (δ⁻).", 0xFFEF4444, "Electronegativity = 3.44 (Pauling)"),
            ModelPart("hydrogen_1", "Hydrogen Atom 1 (H)", "Electropositive terminal atom with 1 proton.", "Lacks strong pull on shared electron pair, resulting in partial positive charge (δ⁺).", 0xFFF1F5F9, "Partial Charge δ⁺"),
            ModelPart("hydrogen_2", "Hydrogen Atom 2 (H)", "Electropositive terminal atom with 1 proton.", "Pairs with oxygen atom to form polar covalent bond.", 0xFFF1F5F9, "Partial Charge δ⁺"),
            ModelPart("covalent_bond_1", "Polar Covalent Bond 1", "Shared pair of valence electrons between Oxygen and Hydrogen.", "Bond length is approximately 0.958 Å (picometers: 95.8 pm).", 0xFF94A3B8, "Bond Length = 0.96 Å"),
            ModelPart("covalent_bond_2", "Polar Covalent Bond 2", "Shared pair of valence electrons between Oxygen and Hydrogen.", "Dipole vector reinforces the overall molecular dipole moment.", 0xFF94A3B8, "Bond Length = 0.96 Å")
        ),
        labels = listOf(
            ModelLabel("lbl_oxygen", "Oxygen (δ⁻)", "oxygen_atom", 0f, 1.25f, 0f),
            ModelLabel("lbl_h1", "Hydrogen (δ⁺)", "hydrogen_1", -1.3f, -1.15f, 0f),
            ModelLabel("lbl_h2", "Hydrogen (δ⁺)", "hydrogen_2", 1.3f, -1.15f, 0f)
        ),
        dimensions = ModelDimensions(
            title = "H₂O Molecular Properties",
            formulas = listOf(
                "Bond Angle (θ)" to "104.5° (Bent / V-shaped geometry)",
                "O-H Bond Length" to "0.958 Å (95.8 pm)",
                "Molecular Dipole Moment" to "μ = 1.85 Debye (strongly polar)",
                "Electron Geometry" to "Tetrahedral (2 bond pairs + 2 lone pairs)"
            ),
            sampleMeasurements = listOf(
                "Molar Mass" to "18.015 g/mol",
                "Density at 4°C" to "1.000 g/cm³",
                "Specific Heat Capacity" to "4.184 J/(g·°C)"
            )
        ),
        animationInfo = null,
        educationalInfo = EducationalInfo(
            definition = "Water is a chemical compound consisting of two hydrogen atoms bonded to a single oxygen atom via polar covalent bonds in a bent molecular geometry.",
            keyPoints = listOf(
                "The 104.5° angle deviates from the ideal 109.5° tetrahedral angle due to lone-pair lone-pair repulsion.",
                "Unequal sharing of electrons gives water a strong electric dipole moment (δ⁻ at Oxygen, δ⁺ at Hydrogens).",
                "Enables hydrogen bonding between adjacent molecules, giving water an unusually high boiling point.",
                "Water reaches maximum density at 4°C, causing ice to float on water (crucial for aquatic life in winter)."
            ),
            realWorldApplications = listOf(
                "Universal solvent in biological biochemistry (dissolves ionic salts and polar sugars).",
                "Thermal buffer in Earth's oceans and weather regulation through high heat capacity.",
                "Capillary action in plant xylem transporting water from roots to leaves against gravity."
            ),
            teacherTips = listOf(
                "Rotate the molecule to show the planar bent shape vs the 3D tetrahedral electron cloud.",
                "Explain why the lone electron pairs on Oxygen push the two Hydrogen atoms closer together from 109.5° to 104.5°."
            ),
            discussionQuestions = listOf(
                "Why is carbon dioxide (CO₂) linear and non-polar, whereas water (H₂O) is bent and polar?",
                "What would happen to marine ecosystems if ice were denser than liquid water?"
            )
        ),
        classLevel = "Grades 9–12",
        tags = listOf("Chemistry", "VSEPR", "Polarity", "Hydrogen Bonding", "Molecules")
    )

    // ==========================================
    // 7. OPTICAL PRISM (Physics)
    // ==========================================
    val OPTICAL_PRISM = ModelObject3D(
        id = "optical_prism",
        name = "Optical Dispersion Prism",
        subjectId = SubjectId.PHYSICS,
        categoryId = "optics",
        description = "Transparent triangular optical element demonstrating refraction and chromatic dispersion of white light into spectrum colors.",
        capabilities = setOf(
            Capability.ROTATE, Capability.ZOOM, Capability.PAN,
            Capability.LABELS, Capability.SELECTION, Capability.DIMENSIONS
        ),
        parts = listOf(
            ModelPart("glass_prism", "Flint Glass Prism", "Triangular prism body made of high-refractive-index optical glass.", "Refracts light rays at both entering and exiting planar boundaries.", 0xFF0284C7, "Refractive Index n ≈ 1.52"),
            ModelPart("refracting_face_left", "Incident Refracting Surface", "First glass-air interface.", "Incident white light slows down upon entering glass and bends toward the normal line.", 0xFF0EA5E9, "First Refraction"),
            ModelPart("refracting_face_right", "Exit Refracting Surface", "Second glass-air interface.", "Light speeds up as it exits glass into air and bends away from the normal, amplifying dispersion.", 0xFF38BDF8, "Second Refraction")
        ),
        labels = listOf(
            ModelLabel("lbl_prism_apex", "Apex Angle (60°)", "glass_prism", 0f, 1.2f, 0.4f),
            ModelLabel("lbl_white_beam", "White Light (Polychromatic)", "refracting_face_left", -1.8f, 0.2f, 0.5f),
            ModelLabel("lbl_rainbow", "Dispersed Visible Spectrum", "refracting_face_right", 1.8f, 0.2f, 0.6f)
        ),
        dimensions = ModelDimensions(
            title = "Prism Dispersion Principles",
            formulas = listOf(
                "Snell's Law" to "n₁ sin(θ₁) = n₂ sin(θ₂)",
                "Cauchy's Equation" to "n(λ) = A + B / λ² (shorter wavelength → higher n)",
                "Angle of Minimum Deviation" to "n = sin((A + D_m)/2) / sin(A/2)",
                "Visible Spectrum Range" to "Violet (400 nm) to Red (700 nm)"
            ),
            sampleMeasurements = listOf(
                "Refractive Index for Red" to "n_red ≈ 1.514",
                "Refractive Index for Violet" to "n_violet ≈ 1.528",
                "Deviation Difference" to "Violet deviates more than Red"
            )
        ),
        animationInfo = null,
        educationalInfo = EducationalInfo(
            definition = "An optical prism is a transparent optical element with flat, polished surfaces that refract light to separate polychromatic light into its component wavelengths.",
            keyPoints = listOf(
                "Dispersion occurs because the refractive index of glass depends on the wavelength of light (chromatic dispersion).",
                "Shorter wavelengths (Violet ~400 nm) travel slower in glass and bend (deviate) the most.",
                "Longer wavelengths (Red ~700 nm) travel faster in glass and bend (deviate) the least.",
                "Disproved the ancient theory that glass 'colored' light: Sir Isaac Newton recombined the spectrum back into white light with an inverted prism."
            ),
            realWorldApplications = listOf(
                "Spectrophotometry: Chemical element identification via emission and absorption spectra in astronomy.",
                "Binoculars and camera periscopes using Porro prisms for total internal reflection (TIR).",
                "Meteorology: Explains the formation of rainbows by spherical water droplets acting as tiny prisms."
            ),
            teacherTips = listOf(
                "Rotate the 3D model to look along the incident beam and see how the rainbow rays fan out from the exit face.",
                "Reinforce the mnemonic ROYGBIV (Red, Orange, Yellow, Green, Blue, Indigo, Violet) and emphasize that violet bends most."
            ),
            discussionQuestions = listOf(
                "Why does violet light refract more than red light when passing through glass?",
                "How did Isaac Newton prove that white light is a mixture of colors rather than glass adding dye to light?"
            )
        ),
        classLevel = "Grades 8–12",
        tags = listOf("Physics", "Optics", "Refraction", "Dispersion", "Spectrum", "Waves")
    )

    // ==========================================
    // ALL SUBJECTS
    // ==========================================
    val SUBJECTS = listOf(
        Subject(
            id = SubjectId.MATHEMATICS,
            name = "Mathematics",
            description = "Solid geometry, 3D polyhedra, mensuration formulas, and planar net transformations.",
            accentColor = SubjectMath,
            iconName = "shapes",
            categories = listOf(
                Category("solid_geometry", SubjectId.MATHEMATICS, "Solid Geometry", "Cubes, cylinders, cones, spheres, prisms, and pyramids"),
                Category("plane_geometry", SubjectId.MATHEMATICS, "Plane Geometry", "Planar polygons, angles, and symmetry nets"),
                Category("measurements", SubjectId.MATHEMATICS, "Mensuration & Calculus", "Surface areas, volumes, and cross-sectional slices")
            )
        ),
        Subject(
            id = SubjectId.BIOLOGY,
            name = "Biology",
            description = "Human organs, cardiovascular circulation, cellular anatomy, and physiological systems.",
            accentColor = SubjectBiology,
            iconName = "favorite",
            categories = listOf(
                Category("human_body", SubjectId.BIOLOGY, "Human Organs & Anatomy", "Heart, lungs, brain, eye, skeleton, and digestive tract"),
                Category("cells", SubjectId.BIOLOGY, "Cellular Biology", "Animal cells, plant cells, and organelles"),
                Category("genetics", SubjectId.BIOLOGY, "Genetics & Molecules", "DNA double helix and chromosome structures")
            )
        ),
        Subject(
            id = SubjectId.PHYSICS,
            name = "Physics",
            description = "Optics, electromagnetism, wave mechanics, forces, and physical phenomena.",
            accentColor = SubjectPhysics,
            iconName = "bolt",
            categories = listOf(
                Category("optics", SubjectId.PHYSICS, "Optics & Light", "Prisms, lenses, mirrors, and light dispersion"),
                Category("mechanics", SubjectId.PHYSICS, "Classical Mechanics", "Levers, pulleys, inclined planes, and gyroscopes"),
                Category("electromagnetism", SubjectId.PHYSICS, "Electromagnetism", "Electric motors, magnetic fields, and circuits")
            )
        ),
        Subject(
            id = SubjectId.CHEMISTRY,
            name = "Chemistry",
            description = "Molecular geometries, VSEPR configurations, crystal lattices, and atomic models.",
            accentColor = SubjectChemistry,
            iconName = "science",
            categories = listOf(
                Category("molecular_structure", SubjectId.CHEMISTRY, "Molecular Geometry", "Water, methane, carbon dioxide, and polarity"),
                Category("atomic_theory", SubjectId.CHEMISTRY, "Atomic Models", "Bohr model, orbitals, and electron shells"),
                Category("crystal_lattices", SubjectId.CHEMISTRY, "Crystal Structures", "Salt NaCl lattice, diamond, and graphite")
            )
        ),
        Subject(
            id = SubjectId.OTHERS,
            name = "General Science & Others",
            description = "Astronomy, Earth sciences, geology, geography, and general laboratory models.",
            accentColor = SubjectOthers,
            iconName = "public",
            categories = listOf(
                Category("astronomy", SubjectId.OTHERS, "Astronomy & Space", "Solar system, planets, and lunar phases"),
                Category("geology", SubjectId.OTHERS, "Earth Sciences", "Earth's internal layers, tectonic plates, and volcanoes")
            )
        )
    )

    val ALL_MODELS = listOf(
        CUBE,
        CYLINDER,
        CONE,
        SPHERE,
        HUMAN_HEART,
        WATER_MOLECULE,
        OPTICAL_PRISM
    )

    fun getMeshForModel(modelId: String): Mesh3D {
        return when (modelId) {
            "cube" -> ShapeGenerators.createCube()
            "cylinder" -> ShapeGenerators.createCylinder()
            "cone" -> ShapeGenerators.createCone()
            "sphere" -> ShapeGenerators.createSphere()
            "heart" -> ShapeGenerators.createHumanHeart()
            "water_molecule" -> ShapeGenerators.createWaterMolecule()
            "optical_prism" -> ShapeGenerators.createOpticalPrism()
            else -> ShapeGenerators.createCube()
        }
    }
}
