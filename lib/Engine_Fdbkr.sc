Engine_Fdbkr : CroneEngine {

	var <synth;

	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		SynthDef(\synth, { |inL, inR, out,
			// hz1=100.0, hz2=300.0, hz3=510.0, hz4=770.0, hz5=1080.0, hz6=1480.0, hz7=2000.0, hz8=2700.0,
			// hz9=3700.0, hz10=5300.0, hz11=7700.0, hz12=7700.0,
			hz1=100.0, hz2=200.0, hz3=300.0, hz4=400.0, hz5=510.0, hz6=630.0, hz7=770.0, hz8=920.0,
			hz9=1270.0, hz10=1720.0, hz11=2700.0, hz12=4400.0,
			a1=1.0, a2=1.0, a3=1.0, a4=1.0, a5=1.0, a6=1.0, a7=1.0, a8=1.0,
			a9=1.0, a10=1.0, a11=1.0, a12=1.0,
            bw = 100,
			mix=0.5, mixFx=0.5, freeze_dur=10.0, freeze_size=5.0|

            var dry, dryb, wet, wetb, fxMix, outMix,
			b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12;

            dry = [In.ar(inL), In.ar(inR)];
            // dry = DFM1.ar(dry, freq, res, gain, type, noiseLevel).softclip;

			// b1 = BPF.ar(dry, hz1, 0.3);
			// b2 = BPF.ar(dry, hz2, 0.3);
			// b3 = BPF.ar(dry, hz3, 0.3);
			// b4 = BPF.ar(dry, hz4, 0.3);
			// b5 = BPF.ar(dry, hz5, 0.3);
			// b6 = BPF.ar(dry, hz6, 0.3);
			// b7 = BPF.ar(dry, hz7, 0.3);
			// b8 = BPF.ar(dry, hz8, 0.3);
			// b9 = BPF.ar(dry, hz9, 0.3);
			// b10 = BPF.ar(dry, hz10, 0.3);
			// b11 = BPF.ar(dry, hz11, 0.3);
			// b12 = BPF.ar(dry, hz12, 0.3);

			b1 = BPF.ar(dry, hz1, bw/hz1);
			b2 = BPF.ar(dry, hz2, bw/hz2);
			b3 = BPF.ar(dry, hz3, bw/hz3);
			b4 = BPF.ar(dry, hz4, bw/hz4);
			b5 = BPF.ar(dry, hz5, bw/hz5);
			b6 = BPF.ar(dry, hz6, bw/hz6);
			b7 = BPF.ar(dry, hz7, bw/hz7);
			b8 = BPF.ar(dry, hz8, bw/hz8);
			b9 = BPF.ar(dry, hz9, bw/hz9);
			b10 = BPF.ar(dry, hz10, bw/hz10);
			b11 = BPF.ar(dry, hz11, bw/hz11);
			b12 = BPF.ar(dry, hz12, bw/hz12);

            dryb = Mix.new([
				b1 * a1,
				b2 * a2,
				b3 * a3,
				b4 * a4,
				b5 * a5,
				b6 * a6,
				b7 * a7,
				b8 * a8,
				b9 * a9,
				b10 * a10,
				b11 * a11,
				b12 * a12
			]);

            wet = JPverb.ar(dryb, freeze_dur, 0, 5.0, 0.70, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 1.0);

            b1 = BPF.ar(wet, hz1, bw/hz1);
			b2 = BPF.ar(wet, hz2, bw/hz2);
			b3 = BPF.ar(wet, hz3, bw/hz3);
			b4 = BPF.ar(wet, hz4, bw/hz4);
			b5 = BPF.ar(wet, hz5, bw/hz5);
			b6 = BPF.ar(wet, hz6, bw/hz6);
			b7 = BPF.ar(wet, hz7, bw/hz7);
			b8 = BPF.ar(wet, hz8, bw/hz8);
			b9 = BPF.ar(wet, hz9, bw/hz9);
			b10 = BPF.ar(wet, hz10, bw/hz10);
			b11 = BPF.ar(wet, hz11, bw/hz11);
			b12 = BPF.ar(wet, hz12, bw/hz12);

            wetb = Mix.new([
				b1 * a1,
				b2 * a2,
				b3 * a3,
				b4 * a4,
				b5 * a5,
				b6 * a6,
				b7 * a7,
				b8 * a8,
				b9 * a9,
				b10 * a10,
				b11 * a11,
				b12 * a12
			]);
			fxMix = Mix.new([
                dryb * Clip.kr(1 - mixFx),
                (wetb * mixFx * 2).softclip
			]);
			outMix = Mix.new([
                dry * Clip.kr(1 - mix),
                (fxMix * mix * (300/bw) *  (12 / (a1 + a2 + a4 + a5 + a6 + a7 + a8 + a9 + a10 + a11 + a12 + 0.01))).softclip
			]);


			Out.ar(out, outMix);
		}).add;

		context.server.sync;

		synth = Synth.new(\synth, [
			\inL, context.in_b[0].index,
			\inR, context.in_b[1].index,
			\out, context.out_b.index],
		context.xg);

		this.addCommand("hz1", "f", {|msg|
			synth.set(\hz1, msg[1]);
		});
		this.addCommand("hz2", "f", {|msg|
			synth.set(\hz2, msg[1]);
		});
		this.addCommand("hz3", "f", {|msg|
			synth.set(\hz3, msg[1]);
		});
		this.addCommand("hz4", "f", {|msg|
			synth.set(\hz4, msg[1]);
		});
		this.addCommand("hz5", "f", {|msg|
			synth.set(\hz5, msg[1]);
		});
		this.addCommand("hz6", "f", {|msg|
			synth.set(\hz6, msg[1]);
		});
		this.addCommand("hz7", "f", {|msg|
			synth.set(\hz7, msg[1]);
		});
		this.addCommand("hz8", "f", {|msg|
			synth.set(\hz8, msg[1]);
		});
		this.addCommand("hz9", "f", {|msg|
			synth.set(\hz9, msg[1]);
		});
		this.addCommand("hz10", "f", {|msg|
			synth.set(\hz10, msg[1]);
		});
		this.addCommand("hz11", "f", {|msg|
			synth.set(\hz11, msg[1]);
		});
		this.addCommand("hz12", "f", {|msg|
			synth.set(\hz12, msg[1]);
		});

		this.addCommand("a1", "f", {|msg|
			synth.set(\a1, msg[1]);
		});
		this.addCommand("a2", "f", {|msg|
			synth.set(\a2, msg[1]);
		});
		this.addCommand("a3", "f", {|msg|
			synth.set(\a3, msg[1]);
		});
		this.addCommand("a4", "f", {|msg|
			synth.set(\a4, msg[1]);
		});
		this.addCommand("a5", "f", {|msg|
			synth.set(\a5, msg[1]);
		});
		this.addCommand("a6", "f", {|msg|
			synth.set(\a6, msg[1]);
		});
		this.addCommand("a7", "f", {|msg|
			synth.set(\a7, msg[1]);
		});
		this.addCommand("a8", "f", {|msg|
			synth.set(\a8, msg[1]);
		});
		this.addCommand("a9", "f", {|msg|
			synth.set(\a9, msg[1]);
		});
		this.addCommand("a10", "f", {|msg|
			synth.set(\a10, msg[1]);
		});
		this.addCommand("a11", "f", {|msg|
			synth.set(\a11, msg[1]);
		});
		this.addCommand("a12", "f", {|msg|
			synth.set(\a12, msg[1]);
		});

		this.addCommand("bw", "f", {|msg|
			synth.set(\bw, msg[1]);
		});

		this.addCommand("mixFx", "f", {|msg|
			synth.set(\mixFx, msg[1]);
		});
		this.addCommand("mix", "f", {|msg|
			synth.set(\mix, msg[1]);
		});

		this.addCommand("freeze_dur", "f", {|msg|
			synth.set(\freeze_dur, msg[1]);
		});
		this.addCommand("freeze_size", "f", {|msg|
			synth.set(\freeze_size, msg[1]);
		});
	}

	free {

            synth.free;
	}

}
