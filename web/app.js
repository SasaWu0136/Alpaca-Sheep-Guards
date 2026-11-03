'use strict';
const $ = id => document.getElementById(id);
const form = $('farmForm');
const reducedMotion = matchMedia('(prefers-reduced-motion: reduce)').matches;
const stateNames = {VIC:'VICTORIA',NSW:'NEW SOUTH WALES',SA:'SOUTH AUSTRALIA',WA:'WESTERN AUSTRALIA'};
const money = value => new Intl.NumberFormat('en-AU',{style:'currency',currency:'AUD',maximumFractionDigits:2,minimumFractionDigits:2}).format(value);
const escapeHTML = value => String(value).replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
let data = null, selected = 2, playing = false, elapsed = 0, trial = 0, lastFrame = 0, lastPaint = 0, requestId = 0;
let animals = [], pending = false, dirty = false;
const trialDuration = 2600;
const canvas = $('farmCanvas');
const ctx = canvas.getContext('2d');

function readSettings() {
  const fields = new FormData(form);
  return {name:String(fields.get('name')).trim(),state:fields.get('state'),sheep:Number(fields.get('sheep')),lambs:Number(fields.get('lambs')),seed:String(fields.get('seed'))};
}
function updateControls(changed) {
  const other = changed === 'sheep' ? 'lambs' : 'sheep';
  if (changed && Number($(changed).value)+Number($(other).value)>100) $(other).value=100-Number($(changed).value);
  for (const type of ['sheep','lambs']) $(type+'Value').textContent=$(type).value;
  const count=Number($('sheep').value)+Number($('lambs').value);
  $('flockNote').textContent=count+' animals · '+(count===100?'a full little paddock':'up to 100 in your paddock');
}
function markDirty() {
  dirty=true;
  if (data) {
    $('comparisonNote').textContent='Settings changed. Results below still show '+data.name+' · '+data.state+' · '+data.sheep+' sheep + '+data.lambs+' lambs · seed '+data.seed+'. Run again to update.';
    $('runButton').innerHTML='<span aria-hidden="true">▶</span> Update simulation <span class="button-spark" aria-hidden="true">✦</span>';
  } else { buildAnimals(readSettings()); updateSign(readSettings()); }
}
for (const type of ['sheep','lambs']) $(type).addEventListener('input',()=>{updateControls(type);markDirty();});
form.addEventListener('input',e=>{if (!['sheep','lambs'].includes(e.target.id) && e.target.name!=='guards') markDirty();});
form.addEventListener('change',e=>{if(e.target.name==='guards') selectStrategy(Number(e.target.value));});
$('shuffle').addEventListener('click',()=>{$('seed').value=Math.floor(Math.random()*1000000);markDirty();});
function updateSign(settings) {
  $('signName').textContent=settings.name || 'Your little farm';
  $('signState').textContent=stateNames[settings.state]+' · '+selected+' GUARD ALPACA'+(selected===1?'':'S');
}
function selectStrategy(guards) {
  selected=guards;
  form.querySelector('input[name="guards"][value="'+guards+'"]').checked=true;
  document.querySelectorAll('.result-card').forEach(card=>{
    const active=Number(card.dataset.guards)===guards;
    card.classList.toggle('selected',active);card.setAttribute('aria-pressed',String(active));
  });
  updateSign(data||readSettings());
  if(data) {renderPredators();resetPlayback(false);} else buildAnimals(readSettings());
}
function renderCards() {
  const max=data?Math.max(1,...data.scenarios.flatMap(s=>s.costs)):4000;
  $('resultCards').innerHTML=[0,1,2].map(guards=>{
    const result=data?.scenarios[guards];
    const costs=result?result.costs:Array(10).fill(0);
    const best=data?.best===guards;
    const name=['No guards','1 alpaca','2 alpacas'][guards];
    return '<article role="button" tabindex="0" aria-pressed="'+(guards===selected)+'" aria-label="Preview '+name+'" class="result-card '+(best?'best ':'')+(guards===selected?'selected':'')+'" data-guards="'+guards+'">'+
      (best?'<span class="best-label">♛ Lowest mean cost</span>':'')+
      '<div class="card-content"><div class="strategy-symbol"><b class="'+(guards===2?'pair':'')+'" aria-hidden="true">'+['🌿','🦙','🦙🦙'][guards]+'</b><strong>'+name+'</strong></div><div><span class="card-kicker">Mean total cost</span><div class="card-number">'+(result?money(result.meanCost).replace(/(\.\d\d)$/,'<small>$1</small>'):'—')+'</div><div class="loss-row"><span>Sheep lost</span><b>'+(result?result.sheepLost.toFixed(1):'—')+'</b></div><div class="loss-row"><span>Lambs lost</span><b>'+(result?result.lambsLost.toFixed(1):'—')+'</b></div></div><div class="card-bars"><span>Cost / trial · AUD</span><div class="bars" role="img" aria-label="'+(result?escapeHTML(name+' trial costs: '+costs.map(c=>money(c)).join(', ')):'No results yet')+'">'+costs.map((cost,i)=>'<i data-height="'+Math.max(2,cost/max*100)+'" title="Trial '+(i+1)+': '+money(cost)+'"></i>').join('')+'</div><div class="chart-axis"><span>1</span><span>5</span><span>10</span></div></div></div><div class="card-footer"><span>'+(result?'Range '+money(Math.min(...costs))+' – '+money(Math.max(...costs)):'Waiting for your first simulation')+'</span><span>Preview ↗</span></div></article>';
  }).join('');
  $('resultCards').querySelectorAll('.bars i').forEach(bar=>{bar.style.height=bar.dataset.height+'%';});
  $('resultCards').querySelectorAll('.result-card').forEach(card=>{
    const activate=()=>selectStrategy(Number(card.dataset.guards));
    card.addEventListener('click',activate);
    card.addEventListener('keydown',e=>{if(e.key==='Enter'||e.key===' '){e.preventDefault();activate();}});
  });
}
function renderPredators() {
  const result=data?.scenarios[selected];
  const species=[['fox','🦊','Fox'],['dingo','🐕','Dingo'],['feral pig','🐗','Feral pig'],['wedge-tailed eagle','🦅','Eagle']];
  $('predatorStats').innerHTML=species.map(([key,icon,label])=>'<div class="predator-stat"><span aria-hidden="true">'+icon+'</span><b>'+(result?result.predators[key].toFixed(1):'—')+'</b><small>'+label+'</small></div>').join('');
}
async function runSimulation(event) {
  if(event)event.preventDefault();
  if(pending || !form.reportValidity())return;
  const settings=readSettings();
  if(settings.sheep+settings.lambs<1){$('formError').textContent='Your paddock needs at least one sheep or lamb.';return;}
  if(!/^-?\d+$/.test(settings.seed)){ $('formError').textContent='Please use a whole number for the seed.';return; }
  pending=true;playing=false;const id=++requestId;
  $('runButton').disabled=true;$('runButton').textContent='Calculating three strategies…';$('formError').textContent='';
  try {
    const response=await fetch('/api/simulate?'+new URLSearchParams(settings),{signal:AbortSignal.timeout(15000)});
    if(!response.ok){let message='Unable to run the simulation.';try{message=(await response.json()).error||message;}catch{}throw new Error(message);}
    const result=await response.json();if(id!==requestId)return;
    data=result;dirty=JSON.stringify(readSettings())!==JSON.stringify(settings);
    $('comparisonNote').textContent=data.name+' · '+data.state+' · '+data.sheep+' sheep + '+data.lambs+' lambs · seed '+data.seed+' · 10 independent trials per strategy.';
    $('footerSummary').textContent='10 trials per strategy · Seed '+data.seed+' · Original Java engine';
    $('downloadReport').disabled=false;
    const best=data.scenarios[data.best], saving=data.scenarios[0].meanCost-best.meanCost;
    $('insightText').textContent=(data.best===0?'No guards had the lowest mean cost for this sample.':data.best+' alpaca'+(data.best===1?'':'s')+' had the lowest mean cost: '+money(saving)+' less than no guards in this sample.')+' Try another seed to explore how results vary.';
    $('insight').hidden=false;
    renderCards();selectStrategy(selected);resetPlayback(!reducedMotion);
    if(dirty)markDirty();
  } catch(error) {
    $('formError').textContent=error.name==='TimeoutError'?'The local server took too long. Check the Terminal and try again.':error.message;
    $('sceneStatus').textContent='SIMULATION UNAVAILABLE';
  } finally {
    pending=false;$('runButton').disabled=false;
    $('runButton').innerHTML='<span aria-hidden="true">▶</span> '+(dirty?'Update':'Run')+' simulation <span class="button-spark" aria-hidden="true">✦</span>';
  }
}
form.addEventListener('submit',runSimulation);
function buildAnimals(settings) {
  animals=[];
  const count=settings.sheep+settings.lambs;
  // Deterministic visual layout, separate from the Java model's random stream.
  let visualSeed=2183;
  const rand=()=>{visualSeed=(visualSeed*1664525+1013904223)>>>0;return visualSeed/4294967296;};
  for(let i=0;i<count;i++){
    const isLamb=i>=settings.sheep;
    animals.push({type:isLamb?'lamb':'sheep',index:isLamb?i-settings.sheep:i,x:.15+rand()*.69,y:.40+rand()*.37,phase:rand()*6.28,flip:rand()>.5?1:-1});
  }
  for(let i=0;i<selected;i++)animals.push({type:'alpaca',index:i,x:.43+i*.13,y:.56+i*.07,phase:i*2,flip:i===0?1:-1});
  animals.sort((a,b)=>a.y-b.y);
}
function resetPlayback(autoPlay) {
  elapsed=0;trial=0;playing=Boolean(autoPlay);buildAnimals(data||readSettings());
  $('playPause').textContent=playing?'Ⅱ':'▶';$('playPause').setAttribute('aria-label',playing?'Pause trial animation':'Play trial animation');
  $('sceneStatus').textContent=data?(playing?'TRIAL PLAYBACK':'READY TO REPLAY'):'YOUR LITTLE FARM';
  document.querySelector('.live-badge').classList.toggle('playing',playing);
  updateTrialUI(0);paint(0);
}
function playbackState() {
  const position=Math.min(elapsed/trialDuration,9.99999);
  return {index:Math.floor(position),progress:elapsed>=trialDuration*10?1:position%1};
}
function lossLimit(type,index) {
  if(!data)return 0;
  const s=data.scenarios[selected];
  return type==='sheep'?s.sheepLosses[index]:type==='lamb'?s.lambLosses[index]:s.alpacaLosses[index];
}
function updateTrialUI(progress) {
  if(!data){$('trialLabel').textContent='Ready to explore';return;}
  const state=playbackState();trial=state.index;
  const s=data.scenarios[selected];
  const losses=s.sheepLosses[trial]+s.lambLosses[trial];
  const completed=Math.floor(losses*Math.min(1,state.progress/0.8));
  $('trialLabel').textContent='Trial '+String(trial+1).padStart(2,'0')+' / 10';
  $('trialCost').textContent=money(s.costs[trial]);
  $('survivors').innerHTML=(data.sheep+data.lambs-completed)+' <em>/ '+(data.sheep+data.lambs)+'</em>';
  $('trialNote').textContent=elapsed===0?'Press play to explore each trial.':state.progress>=.8?losses+' flock losses in this trial. '+s.alpacaLosses[trial]+' guard losses.':'Replaying '+selected+'-guard strategy. Each trial begins with a fresh flock.';
  $('progressBar').style.width=(elapsed/(trialDuration*10)*100)+'%';
}
function togglePlay() {
  if(!data || pending)return;
  if(elapsed>=trialDuration*10)resetPlayback(false);
  playing=!playing;
  $('playPause').textContent=playing?'Ⅱ':'▶';$('playPause').setAttribute('aria-label',playing?'Pause trial animation':'Play trial animation');
  $('sceneStatus').textContent=playing?'TRIAL PLAYBACK':'PLAYBACK PAUSED';
  document.querySelector('.live-badge').classList.toggle('playing',playing);
}
$('playPause').addEventListener('click',togglePlay);
$('replayButton').addEventListener('click',()=>{if(data)resetPlayback(!reducedMotion);});
// Pixel sprites are drawn in code; the landscape is a generated art asset.
function drawSheep(x,y,scale,flip,alpha,lamb,phase) {
  ctx.save();ctx.translate(Math.round(x),Math.round(y));ctx.scale(scale*flip,scale);ctx.globalAlpha=alpha;
  ctx.fillStyle='#344a2c38';ctx.fillRect(-9,7,20,3);
  const step=Math.sin(phase)>0?1:0;
  ctx.fillStyle='#594b39';ctx.fillRect(-6,3,3,6-step);ctx.fillRect(4,3,3,5+step);
  ctx.fillStyle=lamb?'#dccba2':'#d1c5a2';ctx.fillRect(-10,-6,19,10);ctx.fillRect(-7,-9,13,4);
  ctx.fillStyle=lamb?'#fff5d9':'#fffbea';ctx.fillRect(-9,-7,15,9);ctx.fillRect(-6,-10,9,4);ctx.fillRect(-12,-4,4,5);ctx.fillRect(-5,-5,13,9);
  ctx.fillStyle='#e7dbba';ctx.fillRect(-7,-6,2,2);ctx.fillRect(-2,-8,2,2);ctx.fillRect(2,-4,2,2);
  ctx.fillStyle='#9e8c6a';ctx.fillRect(7,-6,6,8);ctx.fillRect(11,-4,4,4);
  ctx.fillStyle='#d6c8a4';ctx.fillRect(6,-9,4,3);ctx.fillStyle='#302d24';ctx.fillRect(10,-5,2,2);
  ctx.restore();
}
function drawAlpaca(x,y,scale,flip,alpha,scarf,phase) {
  ctx.save();ctx.translate(Math.round(x),Math.round(y));ctx.scale(scale*flip,scale);ctx.globalAlpha=alpha;
  ctx.fillStyle='#344a2c38';ctx.fillRect(-10,9,25,4);
  ctx.fillStyle='#bca987';ctx.fillRect(-8,0,4,12);ctx.fillRect(4,0,4,12);
  ctx.fillStyle='#e9d8b2';ctx.fillRect(-12,-13,24,15);ctx.fillRect(5,-30,8,25);
  ctx.fillStyle='#fff3d5';ctx.fillRect(-10,-15,20,14);ctx.fillRect(7,-32,7,25);ctx.fillRect(6,-36,13,10);ctx.fillRect(16,-33,5,6);
  ctx.fillStyle='#d0b98e';ctx.fillRect(7,-42,3,8);ctx.fillRect(14,-42,3,8);
  ctx.fillStyle='#f5e3c2';ctx.fillRect(8,-41,2,6);ctx.fillRect(15,-41,2,6);
  ctx.fillStyle='#34372a';ctx.fillRect(15,-34,2,2);ctx.fillStyle=scarf;ctx.fillRect(5,-24,10,4);ctx.fillRect(5,-20,4,9);
  ctx.fillStyle='#ddc9a3';ctx.fillRect(-13,-15,5,3);ctx.fillRect(-10,-17,4,3);
  ctx.restore();
}
function drawFox(x,y,scale,time) {
  ctx.save();ctx.translate(Math.round(x),Math.round(y));ctx.scale(scale,scale);
  ctx.fillStyle='#52613930';ctx.fillRect(-14,8,34,3);
  ctx.fillStyle='#984c24';ctx.fillRect(-6,1,3,8);ctx.fillRect(10,1,3,8);
  ctx.fillStyle='#db7e35';ctx.fillRect(-9,-8,25,11);ctx.fillRect(10,-15,9,11);ctx.fillRect(17,-10,7,5);ctx.fillRect(-18,-11,12,6);
  ctx.fillStyle='#fbdfaa';ctx.fillRect(-20,-11,5,5);ctx.fillRect(17,-6,6,2);ctx.fillRect(10,-4,7,5);
  ctx.fillStyle='#713f25';ctx.fillRect(11,-20,3,7);ctx.fillRect(17,-19,3,6);ctx.fillStyle='#242e20';ctx.fillRect(17,-12,2,2);ctx.fillRect(23,-9,2,3);ctx.restore();
}
function paint(time) {
  const w=canvas.clientWidth,h=canvas.clientHeight,dpr=Math.min(devicePixelRatio||1,2);
  if(canvas.width!==Math.round(w*dpr)||canvas.height!==Math.round(h*dpr)){canvas.width=Math.round(w*dpr);canvas.height=Math.round(h*dpr);}
  ctx.setTransform(dpr,0,0,dpr,0,0);ctx.clearRect(0,0,w,h);ctx.imageSmoothingEnabled=false;
  const ps=playbackState();
  const scale=Math.max(.62,Math.min(1.35,w/760));
  for(const a of animals){
    const limit=lossLimit(a.type,ps.index);
    const disappear=a.index<limit;
    const threshold=limit?(a.index+1)/limit*.7:1;
    const alpha=disappear?Math.max(0,Math.min(1,(threshold-ps.progress)*7+1)):1;
    if(alpha===0)continue;
    const t=reducedMotion?0:time/1000;
    const wobble=Math.sin(t*.55+a.phase)*3*scale;
    const x=a.x*w+wobble,y=a.y*h+Math.sin(t*.4+a.phase)*2;
    if(a.type==='alpaca')drawAlpaca(x,y,scale*1.2,a.flip,alpha,a.index===0?'#bd5f45':'#4a8050',t*2+a.phase);
    else drawSheep(x,y,scale*(a.type==='lamb'?.67:.9),a.flip,alpha,a.type==='lamb',t*2+a.phase);
  }
  drawFox(w*.86+(reducedMotion?0:Math.sin(time/3500)*12),h*.88,scale, time);
}
function frame(timestamp) {
  const dt=lastFrame?Math.min(timestamp-lastFrame,120):0;lastFrame=timestamp;
  if(playing&&!document.hidden){elapsed=Math.min(trialDuration*10,elapsed+dt*Number($('speed').value));
    if(elapsed>=trialDuration*10){playing=false;$('sceneStatus').textContent='PLAYBACK COMPLETE';$('playPause').textContent='▶';$('playPause').setAttribute('aria-label','Replay trial animation');document.querySelector('.live-badge').classList.remove('playing');}
  }
  if(timestamp-lastPaint>50){paint(timestamp);updateTrialUI();lastPaint=timestamp;}
  requestAnimationFrame(frame);
}
document.addEventListener('visibilitychange',()=>{lastFrame=0;});
$('aboutButton').addEventListener('click',()=>$('aboutDialog').showModal());
$('closeAbout').addEventListener('click',()=>$('aboutDialog').close());
$('aboutDialog').addEventListener('click',e=>{if(e.target===$('aboutDialog')){const box=$('aboutDialog').getBoundingClientRect();if(e.clientX<box.left||e.clientX>box.right||e.clientY<box.top||e.clientY>box.bottom)$('aboutDialog').close();}});
$('downloadReport').addEventListener('click',()=>{
  if(!data)return;
  const lines=['ALPACA GUARD — SIMULATION REPORT','Farm: '+data.name,'State: '+data.state,'Sheep: '+data.sheep,'Lambs: '+data.lambs,'Seed: '+data.seed,'Trials per strategy: 10','Engine: original Java Simulation',''];
  for(const s of data.scenarios){lines.push('GUARD ALPACAS: '+s.guards,'Mean cost (AUD model units): '+money(s.meanCost),'Mean sheep lost: '+s.sheepLost,'Mean lambs lost: '+s.lambsLost,'Mean alpacas lost: '+s.alpacasLost,'Trial costs: '+s.costs.map(c=>c.toFixed(2)).join(', '),'Predator mean kills: '+JSON.stringify(s.predators),'');}
  lines.push('Lowest sample mean: '+data.best+' guard alpaca(s)','Educational simulation. Inputs are supplied assumptions, not field-validated forecasts.');
  const blob=new Blob([lines.join('\n')],{type:'text/plain;charset=utf-8'});const url=URL.createObjectURL(blob);const a=document.createElement('a');a.href=url;a.download='alpaca-guard-seed-'+data.seed+'.txt';a.click();setTimeout(()=>URL.revokeObjectURL(url),1000);
});
updateControls();renderCards();renderPredators();buildAnimals(readSettings());updateSign(readSettings());requestAnimationFrame(frame);runSimulation();
