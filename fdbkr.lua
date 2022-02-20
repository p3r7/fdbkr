-- fdbkr.
-- @eigen
--
--


-- -------------------------------------------------------------------------
-- REQUIRES

engine.name = "Fdbkr"

local cs = require 'controlspec'
local _16n = include "sines/lib/16n"


-- -------------------------------------------------------------------------
-- STATIC CONF

local NB_BANDS = 12


-- -------------------------------------------------------------------------
-- STATE

local fps = 14
local redraw_clock
local screen_dirty = false


-- -------------------------------------------------------------------------
-- CORE

function findfunction(x)
  assert(type(x) == "string")
  local f=_G
  for v in x:gmatch("[^%.]+") do
    if type(f) ~= "table" then
      return nil, "looking for '"..v.."' expected table, not "..type(f)
    end
    f=f[v]
  end
  if type(f) == "function" then
    return f
  else
    return nil, "expected function, not "..type(f)
  end
end


-- -------------------------------------------------------------------------
-- INIT / CLEANUP

local og_mon_level=0.0

function init()
  params:add_control("bw", "bw", cs.def{
                       min=10,
                       max=300,
                       warp='lin',
                       step=20,
                       default=100
  })
  params:set_action("bw", function(v)
                      engine.bw(v)
  end)
  params:add_control("freeze_dur", "freeze dur", cs.def{
                       min=0.1,
                       max=60.0,
                       warp='lin',
                       step=0.5,
                       default=10.0
  })
  params:set_action("freeze_dur", function(v)
                      engine.freeze_dur(v)
  end)
  params:add_control("freeze_size", "freeze size", cs.def{
                       min=0.5,
                       max=5.0,
                       warp='lin',
                       step=0.5,
                       default=0.5
  })
  params:set_action("freeze_size", function(v)
                      engine.freeze_size(v)
  end)
  params:add_control("mix_fx", "mix fx", cs.def{
                       min=0.0,
                       max=1.0,
                       warp='lin',
                       step=0.1,
                       default=0.5
  })
  params:set_action("mix_fx", function(v)
                      engine.mixFx(v)
  end)
  params:add_control("mix", "mix", cs.def{
                       min=0.0,
                       max=1.0,
                       warp='lin',
                       step=0.1,
                       default=0.5
  })
  params:set_action("mix", function(v)
                      engine.mix(v)
  end)

  for b=1,NB_BANDS do
    params:add_control("hz"..b, "hz"..b, controlspec.new(200.0, 20000, "exp", 0, 20000))
    params:set_action("hz"..b, function(v)
                        local engine_setter = 'engine.hz'..b
                        assert(findfunction(engine_setter))(v)
    end)

    params:add_control("a"..b, "a"..b, controlspec.new(0.00, 1.00, "lin", 0.01, .80))
    params:set_action("a"..b, function(v)
                        local engine_setter = 'engine.a'..b
                        assert(findfunction(engine_setter))(v)
    end)
  end

  _16n.init(_16n_slider_callback)
  params:add{type = "option", id = "16n_auto", name = "auto bind 16n",
             options = {"yes", "no"}, default = 1}

  og_mon_level = params:get("monitor_level")
  params:set("monitor_level", -inf)

  redraw_clock = clock.run(
    function()
      local step_s = 1 / fps
      while true do
        clock.sleep(step_s)
        if screen_dirty then
          redraw()
          screen_dirty = false
        end
      end
  end)
end

function cleanup()
  clock.cancel(redraw_clock)
  params:set("monitor_level", og_mon_level)
end


-- -------------------------------------------------------------------------
-- MAIN

function _16n_slider_callback(midi_msg)
  local slider_id = _16n.cc_2_slider_id(midi_msg.cc)
  local v = midi_msg.val

  if params:string("16n_auto") == "no" then
    return
  end

  if slider_id == 13 then
    -- params:set("freeze_size", util.linlin(0, 127, 0.5, 5.0, v))
    params:set("bw", util.linlin(0, 127, 10, 300, v))
    return
  end
  if slider_id == 14 then
    params:set("freeze_dur", util.linlin(0, 127, 0.1, 20.0, v))
    return
  end
  if slider_id == 15 then
    params:set("mix_fx", util.linlin(0, 127, 0.0, 1.0, v))
    return
  end
  if slider_id == 16 then
    params:set("mix", util.linlin(0, 127, 0.0, 1.0, v))
    return
  end

  if slider_id > NB_BANDS then
    return
  end

  params:set("a" .. slider_id, util.linlin(0, 127, 0.00, 1.00, v))
  screen_dirty = true
end

function redraw()
  screen.aa(1)
  screen.line_width(2.0)
  screen.clear()

  for b=1,NB_BANDS do
    local a = params:get("a"..b)
    screen.level(2)
    screen.move(32+(b-1)*4, 62)
    screen.line(32+(b-1)*4, 60-(a*32))
    screen.stroke()
  end

  screen.update()
end
