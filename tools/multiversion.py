#!/usr/bin/env python3
"""Build the legacy 1.21 client and the native 26.3 port in isolated trees."""
import os, pathlib, shutil, subprocess, sys
ROOT=pathlib.Path(__file__).resolve().parent.parent
WORK=ROOT/'build/multiversion'
TARGETS={'1.21.1':'0.104.0+1.21.1','26.3':'0.161.0+26.3'}
COVERS={'1.21.1':['1.21']}
SKIP=set()
def mod_version():
 return next(line.split('=',1)[1].strip() for line in (ROOT/'gradle.properties').read_text().splitlines() if line.startswith('mod_version='))
def build(mc):
 target=WORK/mc
 if target.exists():shutil.rmtree(target)
 target.mkdir(parents=True)
 for name in ['src','gradle','gradlew','gradlew.bat','build.gradle','settings.gradle','gradle.properties','LICENSE']:
  src=ROOT/name; dst=target/name
  if src.is_dir():shutil.copytree(src,dst)
  elif src.exists():shutil.copy2(src,dst)
 if mc=='26.3':
  shutil.copytree(ROOT/'variants/26.3',target,dirs_exist_ok=True)
  p=target/'gradle.properties';s=p.read_text()
  replacements={'minecraft_version':'26.3','minecraft_version_dependency':'26.3','minecraft_version_list':'26.3','fabric_version':TARGETS[mc],'loader_version':'0.19.5','voxelmap_version':'u1l1hMzs','xaeros_version':'fabric-26.3-26.5.3','run_with':'xaeros'}
  lines=[]
  for line in s.splitlines():
   k,sep,v=line.partition('=');lines.append(k+'='+replacements[k] if k in replacements else line)
  p.write_text('\n'.join(lines)+'\n')
  p=target/'gradle/wrapper/gradle-wrapper.properties';p.write_text(p.read_text().replace('gradle-8.10.1','gradle-9.6.0'))
 java=pathlib.Path(os.environ.get('JAVA_HOME_25_X64' if mc=='26.3' else 'JAVA_HOME_21_X64',str(pathlib.Path.home()/('.local/opt/jdk-25' if mc=='26.3' else '.local/opt/jdk-21'))))
 env=dict(os.environ,JAVA_HOME=str(java));env['PATH']=str(java/'bin')+os.pathsep+env.get('PATH','')
 with (target/'build.log').open('w') as log:
  proc=subprocess.run(['./gradlew','build','--console=plain'],cwd=target,env=env,stdout=log,stderr=subprocess.STDOUT)
 if proc.returncode:
  print((target/'build.log').read_text()[-24000:]);return False
 jars=[p for p in (target/'build/libs').glob('*.jar') if not p.name.endswith('-sources.jar')]
 for jar in jars:shutil.copy2(jar,WORK/jar.name);print(WORK/jar.name)
 return True
if __name__=='__main__':
 versions=sys.argv[1:] or TARGETS
 for mc in versions:
  if mc not in TARGETS:raise SystemExit('Unsupported target: '+mc)
  if not build(mc):sys.exit(1)
